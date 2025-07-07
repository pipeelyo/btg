package com.example.btg.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InversionRequest {
    @NotNull(message = "El ID del fondo de inversión es requerido")
    private String fondoInversionId;
    
    @NotNull(message = "El monto a invertir es requerido")
    private BigDecimal montoInvertido;
}
