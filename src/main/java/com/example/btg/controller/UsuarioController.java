package com.example.btg.controller;

import com.example.btg.model.Usuario;
import com.example.btg.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> buscarUsuarios(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String email) {
        
        if (nombre != null && !nombre.trim().isEmpty()) {
            return ResponseEntity.ok(usuarioService.buscarPorNombre(nombre));
        } else if (email != null && !email.trim().isEmpty()) {
            return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/rol/{rol}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> buscarPorRol(@PathVariable String rol) {
        try {
            Usuario.Rol rolEnum = Usuario.Rol.valueOf(rol.toUpperCase());
            return ResponseEntity.ok(usuarioService.buscarPorRol(rolEnum));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
