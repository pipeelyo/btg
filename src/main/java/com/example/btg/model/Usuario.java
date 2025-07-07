package com.example.btg.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Data
@Document(collection = "usuarios")
public class Usuario implements UserDetails {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String email;
    
    private String password;
    private String nombre;
    private String apellido;
    private List<Rol> roles;
    private String telefono;
    private boolean activo = true;
    private BigDecimal saldo = new BigDecimal("500000.00"); // Saldo inicial COP $500.000
    private PreferenciasNotificacion preferenciasNotificacion = new PreferenciasNotificacion();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(rol -> new SimpleGrantedAuthority("RO_" + rol.name()))
                .toList();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return activo;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }

    public enum Rol {
        ADMIN,
        CLIENTE
    }
    
    @Data
    public static class PreferenciasNotificacion {
        private boolean notificarPorEmail = true;
        private boolean notificarPorSMS = false;
        private Set<TipoNotificacion> notificacionesHabilitadas = EnumSet.allOf(TipoNotificacion.class);
        
        public boolean estaHabilitada(TipoNotificacion tipo) {
            return notificacionesHabilitadas.contains(tipo);
        }
    }
    
    public enum TipoNotificacion {
        SUSCRIPCION_CREADA,
        SUSCRIPCION_CANCELADA,
        LIQUIDACION_SOLICITADA,
        ESTADO_CUENTA,
        NOTICIAS_PROMOCIONES
    }
}
