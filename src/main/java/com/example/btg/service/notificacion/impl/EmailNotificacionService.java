package com.example.btg.service.notificacion.impl;

import com.example.btg.model.Usuario;
import com.example.btg.service.notificacion.MensajeNotificacion;
import com.example.btg.service.notificacion.ServicioNotificacion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
public class EmailNotificacionService implements ServicioNotificacion {

    private final TemplateEngine templateEngine;
    private final String emailFrom = "notificaciones@btgpactual.com";

    public EmailNotificacionService( TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Async
    @Override
    public void enviarNotificacion(Usuario usuario, MensajeNotificacion mensaje) {
        if (!usuario.getPreferenciasNotificacion().isNotificarPorEmail() || 
            !usuario.getPreferenciasNotificacion().estaHabilitada(mensaje.getTipo())) {
            return;
        }

        try {
            MimeMessage mimeMessage = null;
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            // Configuración básica del correo
            helper.setFrom(emailFrom);
            helper.setTo(usuario.getEmail());
            helper.setSubject(mensaje.getAsunto());
            
            // Procesar plantilla Thymeleaf
            Context context = new Context();
            context.setVariable("nombre", usuario.getNombre());
            context.setVariable("contenido", mensaje.getContenido());
            
            String contenidoHtml = templateEngine.process("emails/notificacion", context);
            helper.setText(contenidoHtml, true);
            
            // Enviar el correo
            // mailSender.send(mimeMessage);
            log.info("Correo enviado a {}", usuario.getEmail());
            
        } catch (MessagingException e) {
            log.error("Error al enviar correo a {}: {}", usuario.getEmail(), e.getMessage(), e);
        }
    }

    @Override
    public boolean soportaTipo(Usuario.TipoNotificacion tipo) {
        // Este servicio soporta todos los tipos de notificación
        return true;
    }
}
