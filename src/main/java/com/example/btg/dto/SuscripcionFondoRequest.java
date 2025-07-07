package com.example.btg.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SuscripcionFondoRequest {
    
    @NotBlank(message = "El ID del fondo es obligatorio")
    private String fondoId;
    
    @NotNull(message = "El monto a invertir es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a cero")
    private BigDecimal montoInvertido;
    
    @NotBlank(message = "La fuente de fondos es obligatoria")
    private String fuenteFondos;
    
    private String numeroCuentaOrigen;
    private String bancoOrigen;
    private String numeroTransaccion;
    
    @NotBlank(message = "La periodicidad es obligatoria")
    private String periodicidad; // MENSUAL, BIMESTRAL, TRIMESTRAL, etc.
    
    private boolean aceptaTerminos;
    
    @AssertTrue(message = "Debe aceptar los términos y condiciones")
    public boolean isAceptaTerminos() {
        return aceptaTerminos;
    }
}
