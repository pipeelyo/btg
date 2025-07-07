package com.example.btg.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Document(collection = "inversiones")
public class Inversion {
    @Id
    private String id;
    
    @DBRef
    private Usuario usuario;
    
    @DBRef
    private FondoInversion fondoInversion;
    
    private String numeroCuenta;  // Número de cuenta único para la inversión
    private BigDecimal montoInvertido;
    private Integer cantidadCuotas;
    private BigDecimal valorCuotaInicial;
    private LocalDateTime fechaInversion;
    private LocalDateTime fechaVencimiento;
    private EstadoInversion estado;
    private String tipoSuscripcion; // NUEVA o ADICIONAL
    private String periodicidad;    // MENSUAL, BIMESTRAL, TRIMESTRAL, etc.
    
    // Datos de la fuente de fondos
    private String fuenteFondos;
    private String numeroCuentaOrigen;
    
    // Datos para transferencia bancaria
    private String bancoOrigen;
    private String numeroTransaccion;
    
    // Constructor para nueva suscripción
    public Inversion() {
        this.id = UUID.randomUUID().toString();
        this.fechaInversion = LocalDateTime.now();
        this.estado = EstadoInversion.ACTIVA;
        this.tipoSuscripcion = "NUEVA";
        this.numeroCuenta = generarNumeroCuenta();
    }
    
    // Generar número de cuenta único
    private String generarNumeroCuenta() {
        return "INV" + System.currentTimeMillis() + 
               String.format("%04d", (int)(Math.random() * 10000));
    }
    
    public enum EstadoInversion {
        SOLICITADA,     // Cuando se crea la solicitud
        APROBADA,       // Cuando se aprueba la inversión
        ACTIVA,         // Cuando está activa y generando rendimientos
        LIQUIDADA,      // Cuando se liquida la inversión
        RECHAZADA,      // Cuando se rechaza la solicitud
        CANCELADA       // Cuando se cancela por el usuario
    }
}
