package com.example.btg.dto;

import com.example.btg.model.FondoInversion.TipoFondo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FondoInversionRequest {
    @NotBlank(message = "El código es requerido")
    private String codigo;
    
    @NotBlank(message = "El nombre es requerido")
    private String nombre;
    
    private String descripcion;
    
    @NotNull(message = "El tipo de fondo es requerido")
    private TipoFondo tipoFondo;
    
    @NotNull(message = "El valor de la cuotaparte es requerido")
    @DecimalMin(value = "0.01", message = "El valor de la cuotaparte debe ser mayor a cero")
    private BigDecimal valorCuotaparte;
}
