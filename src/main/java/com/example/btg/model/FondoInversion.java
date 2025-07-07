package com.example.btg.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Document(collection = "fondos_inversion")
public class FondoInversion {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String codigo;
    
    private String nombre;
    private String descripcion;
    private TipoFondo tipoFondo;
    private BigDecimal valorCuotaparte;
    private boolean activo = true;
    
    public enum TipoFondo {
        FPV,        // Fondos de Pensiones Voluntarias
        FIC        // Fondos de Inversión Colectiva
    }
}
