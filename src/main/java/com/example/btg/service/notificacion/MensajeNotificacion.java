package com.example.btg.service.notificacion;

import com.example.btg.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensajeNotificacion {
    private Usuario.TipoNotificacion tipo;
    private String asunto;
    private String contenido;
    private String destinatarioEmail;
    private String destinatarioTelefono;
    
    // Métodos de ayuda para construcción fluida
    public static MensajeNotificacionBuilder builder(Usuario.TipoNotificacion tipo) {
        return new MensajeNotificacionBuilder().tipo(tipo);
    }
}
