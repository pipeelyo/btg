package com.example.btg.service;

import com.example.btg.dto.exception.FondoNoEncontradoException;
import com.example.btg.model.FondoInversion;
import com.example.btg.repository.FondoInversionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FondoInversionService {

    private final FondoInversionRepository fondoInversionRepository;

    public List<FondoInversion> obtenerTodosLosFondosActivos() {
        return fondoInversionRepository.findByActivoTrue();
    }

    public FondoInversion obtenerFondoPorId(String id) {
        return fondoInversionRepository.findById(id)
                .orElseThrow(() -> new FondoNoEncontradoException("Fondo no encontrado con ID: " + id));
    }

    public FondoInversion guardarFondo(FondoInversion fondoInversion) {
        return fondoInversionRepository.save(fondoInversion);
    }

    public FondoInversion actualizarFondo(String id, FondoInversion fondoActualizado) {
        FondoInversion fondoExistente = obtenerFondoPorId(id);
        
        // Actualizar solo los campos necesarios
        fondoExistente.setNombre(fondoActualizado.getNombre());
        fondoExistente.setDescripcion(fondoActualizado.getDescripcion());
        fondoExistente.setTipoFondo(fondoActualizado.getTipoFondo());
        
        return fondoInversionRepository.save(fondoExistente);
    }

    public void desactivarFondo(String id) {
        FondoInversion fondo = obtenerFondoPorId(id);
        fondo.setActivo(false);
        fondoInversionRepository.save(fondo);
    }
}
