package com.example.btg.config;

import com.example.btg.model.FondoInversion;
import com.example.btg.model.Usuario;
import com.example.btg.repository.FondoInversionRepository;
import com.example.btg.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("!test") // No ejecutar en pruebas
public class DataInitializer {

    private final UsuarioRepository usuarioRepository;
    private final FondoInversionRepository fondoInversionRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // Crear usuario administrador si no existe
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setEmail("admin@btg.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNombre("Administrador");
            admin.setApellido("BTG");
            admin.setRoles(List.of(Usuario.Rol.ADMIN));
            usuarioRepository.save(admin);

            // Crear usuario de prueba
            Usuario cliente = new Usuario();
            cliente.setEmail("cliente@ejemplo.com");
            cliente.setPassword(passwordEncoder.encode("cliente123"));
            cliente.setNombre("Cliente");
            cliente.setApellido("Ejemplo");
            cliente.setRoles(List.of(Usuario.Rol.CLIENTE));
            usuarioRepository.save(cliente);
        }

        // Crear fondos de inversión de ejemplo si no existen
        if (fondoInversionRepository.count() == 0) {
            // Fondo de Pensiones Voluntarias (FPV)
            FondoInversion fpv = new FondoInversion();
            fpv.setCodigo("FPV001");
            fpv.setNombre("Fondo de Pensiones Voluntarias BTG");
            fpv.setDescripcion("Fondo de pensiones voluntarias con estrategia de inversión balanceada");
            fpv.setTipoFondo(FondoInversion.TipoFondo.FPV);
            fpv.setValorCuotaparte(new BigDecimal("1500.00"));

            // Fondo de Inversión Colectiva (FIC)
            FondoInversion fic = new FondoInversion();
            fic.setCodigo("FIC001");
            fic.setNombre("Fondo de Inversión Colectiva BTG");
            fic.setDescripcion("Fondo de inversión colectiva con exposición a mercados nacionales e internacionales");
            fic.setTipoFondo(FondoInversion.TipoFondo.FIC);
            fic.setValorCuotaparte(new BigDecimal("2000.00"));

            // Fondo Renta Fija
            FondoInversion rentaFija = new FondoInversion();
            rentaFija.setCodigo("FI001");
            rentaFija.setNombre("Fondo Renta Fija BTG");
            rentaFija.setDescripcion("Fondo de inversión de bajo riesgo con exposición a instrumentos de renta fija");
            rentaFija.setTipoFondo(FondoInversion.TipoFondo.FPV);
            rentaFija.setValorCuotaparte(new BigDecimal("1000.00"));

            // Fondo Renta Variable
            FondoInversion rentaVariable = new FondoInversion();
            rentaVariable.setCodigo("FI002");
            rentaVariable.setNombre("Fondo Accionario BTG");
            rentaVariable.setDescripcion("Fondo de inversión de alto riesgo con exposición a acciones nacionales e internacionales");
            rentaVariable.setTipoFondo(FondoInversion.TipoFondo.FIC);
            rentaVariable.setValorCuotaparte(new BigDecimal("2500.00"));

            fondoInversionRepository.saveAll(List.of(fpv, fic, rentaFija, rentaVariable));
        }
    }
}
