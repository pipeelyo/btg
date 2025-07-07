package com.example.btg.controller;

import com.example.btg.dto.FondoInversionRequest;
import com.example.btg.dto.FondoInversionResponse;
import com.example.btg.model.FondoInversion;
import com.example.btg.service.FondoInversionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fondos")
@RequiredArgsConstructor
public class FondoInversionController {

    private final FondoInversionService fondoInversionService;

    @GetMapping
    public ResponseEntity<List<FondoInversionResponse>> obtenerTodosLosFondos() {
        List<FondoInversion> fondos = fondoInversionService.obtenerTodosLosFondosActivos();
        List<FondoInversionResponse> response = fondos.stream()
                .map(FondoInversionResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FondoInversionResponse> obtenerFondoPorId(@PathVariable String id) {
        FondoInversion fondo = fondoInversionService.obtenerFondoPorId(id);
        return ResponseEntity.ok(FondoInversionResponse.fromEntity(fondo));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FondoInversionResponse> crearFondo(@Valid @RequestBody FondoInversionRequest request) {
        FondoInversion fondo = mapToEntity(request);
        FondoInversion creado = fondoInversionService.guardarFondo(fondo);
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
                
        return ResponseEntity.created(location).body(FondoInversionResponse.fromEntity(creado));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FondoInversionResponse> actualizarFondo(
            @PathVariable String id,
            @Valid @RequestBody FondoInversionRequest request
    ) {
        FondoInversion fondo = mapToEntity(request);
        FondoInversion actualizado = fondoInversionService.actualizarFondo(id, fondo);
        return ResponseEntity.ok(FondoInversionResponse.fromEntity(actualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desactivarFondo(@PathVariable String id) {
        fondoInversionService.desactivarFondo(id);
        return ResponseEntity.noContent().build();
    }
    
    private FondoInversion mapToEntity(FondoInversionRequest request) {
        FondoInversion fondo = new FondoInversion();
        fondo.setCodigo(request.getCodigo());
        fondo.setNombre(request.getNombre());
        fondo.setDescripcion(request.getDescripcion());
        fondo.setTipoFondo(request.getTipoFondo());
        fondo.setValorCuotaparte(request.getValorCuotaparte());
        return fondo;
    }
}
