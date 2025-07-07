package com.example.btg.service;

import com.example.btg.model.Usuario;
import com.example.btg.service.notificacion.MensajeNotificacion;
import com.example.btg.service.notificacion.ServicioNotificacion;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class NotificacionService {
    
    private final List<ServicioNotificacion> serviciosNotificacion;
    
    @Async
    public void enviarNotificacion(Usuario usuario, MensajeNotificacion mensaje) {
        if (usuario == null || mensaje == null) {
            log.warn("Intento de enviar notificación con usuario o mensaje nulo");
            return;
        }
        
        serviciosNotificacion.stream()
            .filter(servicio -> servicio.soportaTipo(mensaje.getTipo()))
            .forEach(servicio -> {
                try {
                    servicio.enviarNotificacion(usuario, mensaje);
                } catch (Exception e) {
                    log.error("Error al enviar notificación con servicio {}: {}", 
                             servicio.getClass().getSimpleName(), e.getMessage(), e);
                }
            });
    }
}
