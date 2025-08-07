package com.example.btg.repository;

import com.example.btg.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    List<Usuario> findByEmailContainingIgnoreCase(String email);
    List<Usuario> findByRolesContaining(Usuario.Rol rol);
}
