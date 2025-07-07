package com.example.btg.service;

import com.example.btg.dto.SuscripcionFondoRequest;
import com.example.btg.dto.exception.FondoNoEncontradoException;
import com.example.btg.dto.exception.SaldoInsuficienteException;
import com.example.btg.model.FondoInversion;
import com.example.btg.model.Inversion;
import com.example.btg.model.Usuario;
import com.example.btg.repository.FondoInversionRepository;
import com.example.btg.repository.InversionRepository;
import com.example.btg.repository.UsuarioRepository;
import com.example.btg.security.AuthUtils;
import com.example.btg.service.notificacion.MensajeNotificacion;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
public class SuscripcionService {

    private final InversionRepository inversionRepository;
    private final UsuarioRepository usuarioRepository;
    private final FondoInversionRepository fondoInversionRepository;
    private final AuthUtils authUtils;

    private final NotificacionService notificacionService;
    
    @Transactional
    public Inversion suscribirAFondo(SuscripcionFondoRequest request) {
        // Obtener el usuario autenticado
        String usuarioId = authUtils.getCurrentUserId();
        Usuario usuario = usuarioRepository.findByEmail(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que el fondo existe y está activo
        FondoInversion fondo = fondoInversionRepository.findByIdAndActivoTrue(request.getFondoId())
                .orElseThrow(() -> new FondoNoEncontradoException("Fondo de inversión no disponible"));
        
        // Validar saldo suficiente
        if (usuario.getSaldo().compareTo(request.getMontoInvertido()) < 0) {
            throw SaldoInsuficienteException.paraFondo(fondo.getNombre());
        }

        // Crear la inversión
        Inversion inversion = new Inversion();
        inversion.setUsuario(usuario);
        inversion.setFondoInversion(fondo);
        inversion.setMontoInvertido(request.getMontoInvertido());
        
        // Calcular cantidad de cuotas
        int cantidadCuotas = request.getMontoInvertido()
                .divide(fondo.getValorCuotaparte(), 2, RoundingMode.HALF_UP)
                .intValue();
        
        inversion.setCantidadCuotas(cantidadCuotas);
        inversion.setValorCuotaInicial(fondo.getValorCuotaparte());
        
        // Configurar fechas
        LocalDateTime ahora = LocalDateTime.now();
        inversion.setFechaInversion(ahora);
        
        // Establecer fecha de vencimiento (por ejemplo, 1 año después)
        inversion.setFechaVencimiento(ahora.plus(1, ChronoUnit.YEARS));
        
        // Configurar detalles de la suscripción
        inversion.setTipoSuscripcion("NUEVA");
        inversion.setPeriodicidad(request.getPeriodicidad());
        inversion.setFuenteFondos(request.getFuenteFondos());
        inversion.setNumeroCuentaOrigen(request.getNumeroCuentaOrigen());
        inversion.setBancoOrigen(request.getBancoOrigen());
        inversion.setNumeroTransaccion(request.getNumeroTransaccion());
        
        // Actualizar saldo del usuario
        usuario.setSaldo(usuario.getSaldo().subtract(request.getMontoInvertido()));
        usuarioRepository.save(usuario);
        
        // Guardar la inversión
        Inversion inversionGuardada = inversionRepository.save(inversion);
        
        // Enviar notificación de suscripción exitosa
        enviarNotificacionSuscripcion(usuario, inversionGuardada, fondo);
        
        return inversionGuardada;
    }

    @Transactional
    public Inversion cancelarSuscripcion(String inversionId) {
        String usuarioId = authUtils.getCurrentUserId();
        Inversion inversion = inversionRepository.findByIdAndUsuarioEmail(inversionId, usuarioId)
                .orElseThrow(() -> new RuntimeException("Inversión no encontrada"));

        // Solo se pueden cancelar inversiones activas o solicitadas
        if (inversion.getEstado() != Inversion.EstadoInversion.ACTIVA && 
            inversion.getEstado() != Inversion.EstadoInversion.SOLICITADA) {
            throw new IllegalStateException("No se puede cancelar una inversión que no esté activa o solicitada");
        }

        // Cambiar el estado a CANCELADA
        inversion.setEstado(Inversion.EstadoInversion.CANCELADA);
        
        // Aquí iría la lógica para reversar el pago si es necesario
        
        Inversion inversionCancelada = inversionRepository.save(inversion);
        
        // Enviar notificación de cancelación
        Usuario usuario = inversion.getUsuario();
        FondoInversion fondo = inversion.getFondoInversion();
        enviarNotificacionCancelacion(usuario, inversionCancelada, fondo);
        
        return inversionCancelada;
    }
    
    private void enviarNotificacionSuscripcion(Usuario usuario, Inversion inversion, FondoInversion fondo) {
        String asunto = String.format("Confirmación de suscripción - %s", fondo.getNombre());
        String contenido = String.format(
            "Estimado %s,<br><br>" +
            "Su suscripción al fondo %s ha sido registrada exitosamente.<br>" +
            "<strong>Detalles de la inversión:</strong><br>" +
            "- Monto invertido: $%,.2f<br>" +
            "- Número de cuotas: %d<br>" +
            "- Valor cuotaparte: $%,.2f<br>" +
            "- Fecha de inversión: %s<br><br>" +
            "Gracias por confiar en BTG Pactual.",
            usuario.getNombre(),
            fondo.getNombre(),
            inversion.getMontoInvertido(),
            inversion.getCantidadCuotas(),
            inversion.getValorCuotaInicial(),
            inversion.getFechaInversion().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        
        MensajeNotificacion mensaje = MensajeNotificacion.builder(Usuario.TipoNotificacion.SUSCRIPCION_CREADA)
                .asunto(asunto)
                .contenido(contenido)
                .destinatarioEmail(usuario.getEmail())
                .destinatarioTelefono(usuario.getTelefono())
                .build();
                
        notificacionService.enviarNotificacion(usuario, mensaje);
    }
    
    private void enviarNotificacionCancelacion(Usuario usuario, Inversion inversion, FondoInversion fondo) {
        String asunto = String.format("Cancelación de suscripción - %s", fondo.getNombre());
        String contenido = String.format(
            "Estimado %s,<br><br>" +
            "Su solicitud de cancelación para el fondo %s ha sido procesada.<br>" +
            "<strong>Detalles de la cancelación:</strong><br>" +
            "- Monto reembolsado: $%,.2f<br>" +
            "- Fecha de cancelación: %s<br><br>" +
            "Si no realizó esta acción, por favor contacte a nuestro servicio al cliente.",
            usuario.getNombre(),
            fondo.getNombre(),
            inversion.getMontoInvertido(),
            LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        
        MensajeNotificacion mensaje = MensajeNotificacion.builder(Usuario.TipoNotificacion.SUSCRIPCION_CANCELADA)
                .asunto(asunto)
                .contenido(contenido)
                .destinatarioEmail(usuario.getEmail())
                .destinatarioTelefono(usuario.getTelefono())
                .build();
                
        notificacionService.enviarNotificacion(usuario, mensaje);
    }
}
