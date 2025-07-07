package com.example.btg.service;

import com.example.btg.dto.TransaccionDTO;
import com.example.btg.model.Inversion;
import com.example.btg.model.Inversion.EstadoInversion;
import com.example.btg.repository.InversionRepository;
import com.example.btg.security.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class HistorialService {

    private final InversionRepository inversionRepository;
    private final AuthUtils authUtils;

    public List<TransaccionDTO> obtenerHistorialCompleto() {
        String usuarioId = authUtils.getCurrentUserId();
        List<Inversion> inversiones = inversionRepository.findByUsuarioId(usuarioId);
        
        return inversiones.stream()
                .flatMap(this::mapearInversionATransacciones)
                .sorted(Comparator.comparing(TransaccionDTO::getFecha).reversed())
                .collect(Collectors.toList());
    }

    public List<TransaccionDTO> obtenerHistorialPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        String usuarioId = authUtils.getCurrentUserId();
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.plusDays(1).atStartOfDay();
        
        List<Inversion> inversiones = inversionRepository
                .findByUsuarioIdAndFechaInversionBetween(usuarioId, inicio, fin);
        
        return inversiones.stream()
                .flatMap(this::mapearInversionATransacciones)
                .filter(t -> !t.getFecha().isBefore(inicio) && t.getFecha().isBefore(fin))
                .sorted(Comparator.comparing(TransaccionDTO::getFecha).reversed())
                .collect(Collectors.toList());
    }

    public List<TransaccionDTO> obtenerHistorialPorTipo(String tipo) {
        String usuarioId = authUtils.getCurrentUserId();
        List<Inversion> inversiones = inversionRepository.findByUsuarioId(usuarioId);
        
        return inversiones.stream()
                .flatMap(this::mapearInversionATransacciones)
                .filter(t -> t.getTipo().equalsIgnoreCase(tipo))
                .sorted(Comparator.comparing(TransaccionDTO::getFecha).reversed())
                .collect(Collectors.toList());
    }

    private Stream<TransaccionDTO> mapearInversionATransacciones(Inversion inversion) {
        Stream.Builder<TransaccionDTO> transacciones = Stream.builder();
        
        // Transacción de suscripción
        transacciones.add(TransaccionDTO.fromInversion(inversion, "SUSCRIPCION"));
        
        // Si está cancelada, agregar transacción de cancelación
        if (inversion.getEstado() == EstadoInversion.CANCELADA || 
            inversion.getEstado() == EstadoInversion.LIQUIDADA) {
            transacciones.add(crearTransaccionCancelacion(inversion));
        }
        
        return transacciones.build();
    }
    
    private TransaccionDTO crearTransaccionCancelacion(Inversion inversion) {
        TransaccionDTO dto = TransaccionDTO.fromInversion(inversion, 
            inversion.getEstado() == EstadoInversion.CANCELADA ? "CANCELACION" : "LIQUIDACION");
        dto.setMonto(inversion.getMontoInvertido().negate());
        return dto;
    }
    
}
