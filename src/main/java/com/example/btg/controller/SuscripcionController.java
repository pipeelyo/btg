package com.example.btg.controller;

import com.example.btg.dto.SuscripcionFondoRequest;
import com.example.btg.model.Inversion;
import com.example.btg.service.SuscripcionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suscripciones")
@AllArgsConstructor
public class SuscripcionController {

    private final SuscripcionService suscripcionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CLIENTE')")
    public Inversion suscribirAFondo(@Valid @RequestBody SuscripcionFondoRequest request) {
        return suscripcionService.suscribirAFondo(request);
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('CLIENTE')")
    public Inversion cancelarSuscripcion(@PathVariable String id) {
        return suscripcionService.cancelarSuscripcion(id);
    }
}
