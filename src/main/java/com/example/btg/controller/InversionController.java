package com.example.btg.controller;

import com.example.btg.dto.InversionRequest;
import com.example.btg.dto.InversionResponse;
import com.example.btg.security.AuthUtils;
import com.example.btg.service.InversionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inversiones")
@RequiredArgsConstructor
public class InversionController {

    private final InversionService inversionService;
    private final AuthUtils authUtils;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<InversionResponse> crearInversion(@Valid @RequestBody InversionRequest inversionRequest) {
        String usuarioId = authUtils.getCurrentUserId();
        return ResponseEntity.ok(inversionService.crearInversion(usuarioId, inversionRequest));
    }

    @GetMapping("/mis-inversiones")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<InversionResponse>> obtenerMisInversiones() {
        String usuarioId = authUtils.getCurrentUserId();
        return ResponseEntity.ok(inversionService.obtenerInversionesPorUsuario(usuarioId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<InversionResponse> obtenerInversionPorId(@PathVariable String id) {
        String usuarioId = authUtils.getCurrentUserId();
        return ResponseEntity.ok(inversionService.obtenerInversionPorIdYUsuario(id, usuarioId));
    }

    @PostMapping("/{id}/liquidar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<InversionResponse> liquidarInversion(@PathVariable String id) {
        String usuarioId = authUtils.getCurrentUserId();
        return ResponseEntity.ok(inversionService.liquidarInversion(id, usuarioId));
    }
}
