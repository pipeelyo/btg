package com.example.btg.dto;

import com.example.btg.model.FondoInversion;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FondoInversionResponse {
    private String id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private FondoInversion.TipoFondo tipoFondo;
    private BigDecimal valorCuotaparte;
    private boolean activo;

    public static FondoInversionResponse fromEntity(FondoInversion fondo) {
        FondoInversionResponse response = new FondoInversionResponse();
        response.setId(fondo.getId());
        response.setCodigo(fondo.getCodigo());
        response.setNombre(fondo.getNombre());
        response.setDescripcion(fondo.getDescripcion());
        response.setTipoFondo(fondo.getTipoFondo());
        response.setValorCuotaparte(fondo.getValorCuotaparte());
        response.setActivo(fondo.isActivo());
        return response;
    }
}
