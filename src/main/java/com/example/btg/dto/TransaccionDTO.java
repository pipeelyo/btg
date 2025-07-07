package com.example.btg.dto;

import com.example.btg.model.FondoInversion;
import com.example.btg.model.Inversion;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransaccionDTO {
    private String id;
    private String tipo; // SUSCRIPCION, CANCELACION, LIQUIDACION
    private String estado;
    private LocalDateTime fecha;
    private String numeroCuenta;
    private String fondoNombre;
    private String fondoCodigo;
    private BigDecimal monto;
    private String moneda = "COP";
    private String referencia;

    public static TransaccionDTO fromInversion(Inversion inversion, String tipoTransaccion) {
        TransaccionDTO dto = new TransaccionDTO();
        dto.setId(inversion.getId());
        dto.setTipo(tipoTransaccion);
        dto.setEstado(inversion.getEstado().name());
        dto.setFecha(tipoTransaccion.equals("SUSCRIPCION") ? 
                    inversion.getFechaInversion() : LocalDateTime.now());
        dto.setNumeroCuenta(inversion.getNumeroCuenta());
        
        FondoInversion fondo = inversion.getFondoInversion();
        if (fondo != null) {
            dto.setFondoNombre(fondo.getNombre());
            dto.setFondoCodigo(fondo.getCodigo());
        }
        
        dto.setMonto(inversion.getMontoInvertido());
        dto.setReferencia("TRX" + System.currentTimeMillis());
        
        return dto;
    }
}
