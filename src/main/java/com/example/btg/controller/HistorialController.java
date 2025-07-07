package com.example.btg.controller;

import com.example.btg.dto.TransaccionDTO;
import com.example.btg.service.HistorialService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/historial")
@RequiredArgsConstructor
public class HistorialController {

    private final HistorialService historialService;

    @GetMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public List<TransaccionDTO> obtenerHistorialCompleto() {
        return historialService.obtenerHistorialCompleto();
    }

    @GetMapping("/rango-fechas")
    @PreAuthorize("hasRole('CLIENTE')")
    public List<TransaccionDTO> obtenerHistorialPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return historialService.obtenerHistorialPorRangoFechas(inicio, fin);
    }

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasRole('CLIENTE')")
    public List<TransaccionDTO> obtenerHistorialPorTipo(@PathVariable String tipo) {
        return historialService.obtenerHistorialPorTipo(tipo.toUpperCase());
    }

    // @GetMapping("/fondo/{fondoId}")
    // @PreAuthorize("hasRole('CLIENTE')")
    // public List<TransaccionDTO> obtenerHistorialPorFondo(@PathVariable String fondoId) {
    //     return historialService.obtenerHistorialPorFond(fondoId);
    // }
}
