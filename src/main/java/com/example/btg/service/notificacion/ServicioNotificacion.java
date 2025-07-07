package com.example.btg.service.notificacion;

import com.example.btg.model.Usuario;

public interface ServicioNotificacion {
    void enviarNotificacion(Usuario usuario, MensajeNotificacion mensaje);
    
    default boolean soportaTipo(Usuario.TipoNotificacion tipo) {
        return true;
    }
}
