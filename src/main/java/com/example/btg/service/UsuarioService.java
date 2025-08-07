package com.example.btg.service;

import com.example.btg.model.Usuario;
import com.example.btg.model.Usuario.Rol;
import com.example.btg.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
        
        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.isEnabled(),
                true,
                true,
                usuario.isAccountNonLocked(),
                usuario.getAuthorities()
        );
    }

    public Usuario registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está en uso");
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        // Por defecto, todos los nuevos usuarios son clientes
        if (usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            usuario.setRoles(Collections.singletonList(Rol.CLIENTE));
        }
        
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }
    
    public List<Usuario> buscarPorNombre(String nombre) {
        return usuarioRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    public List<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmailContainingIgnoreCase(email);
    }
    
    public List<Usuario> buscarPorRol(Usuario.Rol rol) {
        return usuarioRepository.findByRolesContaining(rol);
    }

    public Usuario obtenerUsuarioPorId(String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void eliminarUsuario(String id) {
        usuarioRepository.deleteById(id);
    }
}
