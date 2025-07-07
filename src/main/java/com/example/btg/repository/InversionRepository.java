package com.example.btg.repository;

import com.example.btg.model.Inversion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InversionRepository extends MongoRepository<Inversion, String> {
    List<Inversion> findByUsuarioId(String usuarioId);
    Optional<Inversion> findByIdAndUsuarioEmail(String id, String email);
    Optional<Inversion> findByIdAndUsuarioId(String id, String usuarioId);
    List<Inversion> findByUsuarioIdAndFechaInversionBetween(
        String usuarioId, 
        LocalDateTime fechaInicio, 
        LocalDateTime fechaFin
    );
    List<Inversion> findByUsuarioIdAndEstado(String usuarioId, Inversion.EstadoInversion estado);
    List<Inversion> findByUsuarioIdAndFondoInversionId(String usuarioId, String fondoId);
}
