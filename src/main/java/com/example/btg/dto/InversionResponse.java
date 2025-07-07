package com.example.btg.dto;

import com.example.btg.model.Inversion;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record InversionResponse(
        String id,
        String fondoInversionId,
        String fondoNombre,
        BigDecimal montoInvertido,
        int cantidadCuotas,
        BigDecimal valorCuotaInicial,
        LocalDateTime fechaInversion,
        Inversion.EstadoInversion estado
) {
    public static InversionResponse fromEntity(Inversion inversion) {
        return InversionResponse.builder()
                .id(inversion.getId())
                .fondoInversionId(inversion.getFondoInversion().getId())
                .fondoNombre(inversion.getFondoInversion().getNombre())
                .montoInvertido(inversion.getMontoInvertido())
                .cantidadCuotas(inversion.getCantidadCuotas())
                .valorCuotaInicial(inversion.getValorCuotaInicial())
                .fechaInversion(inversion.getFechaInversion())
                .estado(inversion.getEstado())
                .build();
    }
}
