package com.example.btg.service.notificacion.impl;

import com.example.btg.model.Usuario;
import com.example.btg.service.notificacion.MensajeNotificacion;
import com.example.btg.service.notificacion.ServicioNotificacion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SMSNotificacionService implements ServicioNotificacion {
    @Async
    @Override
    public void enviarNotificacion(Usuario usuario, MensajeNotificacion mensaje) {
        if (!usuario.getPreferenciasNotificacion().isNotificarPorSMS() || 
            !usuario.getPreferenciasNotificacion().estaHabilitada(mensaje.getTipo()) ||
            usuario.getTelefono() == null || usuario.getTelefono().trim().isEmpty()) {
            return;
        }

        try {
            // En un entorno real, aquí se haría la llamada al servicio de SMS
            // Por ahora solo lo simulamos
            String telefono = formatearTelefono(usuario.getTelefono());
            
            log.info("Enviando SMS a {}: {}", telefono, mensaje.getContenido());

            log.info("SMS enviado a {}", telefono);
            
        } catch (Exception e) {
            log.error("Error al enviar SMS a {}: {}", usuario.getTelefono(), e.getMessage(), e);
        }
    }
    
    private String formatearTelefono(String telefono) {
        // Eliminar caracteres no numéricos
        String soloNumeros = telefono.replaceAll("[^0-9]", "");
        
        // Asumimos formato internacional si no tiene prefijo
        if (soloNumeros.startsWith("57")) {
            return "+" + soloNumeros;
        } else if (soloNumeros.startsWith("3") && soloNumeros.length() == 10) {
            return "+57" + soloNumeros;
        } else {
            return "+57" + soloNumeros;
        }
    }

    @Override
    public boolean soportaTipo(Usuario.TipoNotificacion tipo) {
        // No enviamos notificaciones promocionales por SMS
        return tipo != Usuario.TipoNotificacion.NOTICIAS_PROMOCIONES;
    }
}
