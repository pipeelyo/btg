package com.example.btg.service;

import com.example.btg.dto.InversionRequest;
import com.example.btg.dto.InversionResponse;
import com.example.btg.dto.exception.FondoNoEncontradoException;
import com.example.btg.model.FondoInversion;
import com.example.btg.model.Inversion;
import com.example.btg.model.Usuario;
import com.example.btg.repository.FondoInversionRepository;
import com.example.btg.repository.InversionRepository;
import com.example.btg.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InversionService {

    private final InversionRepository inversionRepository;
    private final UsuarioRepository usuarioRepository;
    private final FondoInversionRepository fondoInversionRepository;
    private final ObjectMapper objectMapper;    

    public InversionService(InversionRepository inversionRepository, UsuarioRepository usuarioRepository,
            FondoInversionRepository fondoInversionRepository) {
        this.inversionRepository = inversionRepository;
        this.usuarioRepository = usuarioRepository;
        this.fondoInversionRepository = fondoInversionRepository;
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    }

    @Transactional
    public InversionResponse crearInversion(String usuarioId, InversionRequest inversionRequest) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepository.findByEmail(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Verificar que el fondo existe y está activo
        FondoInversion fondo = fondoInversionRepository.findByIdAndActivoTrue(inversionRequest.getFondoInversionId())
                .orElseThrow(() -> new FondoNoEncontradoException("Fondo de inversión no disponible"));
        
        // Calcular cantidad de cuotas
        int cantidadCuotas = inversionRequest.getMontoInvertido()
                .divide(fondo.getValorCuotaparte(), 2, RoundingMode.HALF_UP)
                .intValue();
        
        // Crear la inversión
        Inversion inversion = new Inversion();
        inversion.setUsuario(usuario);
        inversion.setFondoInversion(fondo);
        inversion.setMontoInvertido(inversionRequest.getMontoInvertido());
        inversion.setCantidadCuotas(cantidadCuotas);
        inversion.setValorCuotaInicial(fondo.getValorCuotaparte());
        inversion.setFechaInversion(LocalDateTime.now());
        inversion.setEstado(Inversion.EstadoInversion.ACTIVA);
    
        return InversionResponse.fromEntity(inversionRepository.save(inversion));
    }

    public List<InversionResponse> obtenerInversionesPorUsuario(String usuarioId) {
        return inversionRepository.findByUsuarioId(usuarioId).stream()
                .map(InversionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public InversionResponse obtenerInversionPorIdYUsuario(String id, String usuarioId) {
        Inversion inversion = inversionRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RuntimeException("Inversión no encontrada"));
        return InversionResponse.fromEntity(inversion);
    }

    @Transactional
    public InversionResponse liquidarInversion(String id, String usuarioId) {
        Inversion inversion = inversionRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RuntimeException("Inversión no encontrada"));
        
        if (inversion.getEstado() != Inversion.EstadoInversion.ACTIVA) {
            throw new IllegalStateException("La inversión no está activa");
        }
        
        // Actualizar el estado a liquidada
        inversion.setEstado(Inversion.EstadoInversion.LIQUIDADA);
        Inversion inversionActualizada = inversionRepository.save(inversion);
        
        return InversionResponse.fromEntity(inversionActualizada);
    }
}
