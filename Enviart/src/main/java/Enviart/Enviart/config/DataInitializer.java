package Enviart.Enviart.config;

import Enviart.Enviart.model.Envio;
import Enviart.Enviart.model.Rol;
import Enviart.Enviart.model.Usuario;
import Enviart.Enviart.repository.EnvioRepository;
import Enviart.Enviart.repository.RolRepository;
import Enviart.Enviart.repository.UsuarioRepository;
import Enviart.Enviart.util.enums.EstadoEnvio;
import Enviart.Enviart.util.enums.TipoDocumento;
import Enviart.Enviart.util.enums.TipoRol;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final EnvioRepository envioRepository;

    public DataInitializer(PasswordEncoder passwordEncoder, RolRepository rolRepository,
            UsuarioRepository usuarioRepository, EnvioRepository envioRepository) {
        this.passwordEncoder = passwordEncoder;
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.envioRepository = envioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Crear roles si no existen
        initializeRoles(TipoRol.ADMINISTRADOR);
        initializeRoles(TipoRol.CLIENTE);
        initializeRoles(TipoRol.MENSAJERO);
        initializeRoles(TipoRol.PROVEEDOR);

        // Crear usuario administrador por defecto
        // Crear usuario administrador por defecto
        initializeAdminUser();

        // Crear envíos de prueba
        initializeEnvios();
    }

    private void initializeRoles(TipoRol tipoRol) {
        Optional<Rol> rolOpt = rolRepository.findByTipoRol(tipoRol);
        if (rolOpt.isEmpty()) {
            Rol rol = new Rol();
            rol.setNombreRol(tipoRol.name());
            rol.setTipoRol(tipoRol);
            rolRepository.save(rol);
            System.out.println("✅ Rol " + tipoRol.name() + " creado con éxito.");
        }
    }

    private void initializeAdminUser() {
        String adminEmail = "admin@enviart.com";
        Optional<Usuario> userOpt = usuarioRepository.findByEmail(adminEmail);

        if (userOpt.isEmpty()) {
            Optional<Rol> adminRolOpt = rolRepository.findByTipoRol(TipoRol.ADMINISTRADOR);

            if (adminRolOpt.isPresent()) {
                Usuario admin = new Usuario();
                admin.setEmail(adminEmail);
                admin.setContrasenaUsuario(passwordEncoder.encode("admin123"));
                admin.setDocUsuario("1000521258");
                admin.setTipoDocumento(TipoDocumento.CC);
                admin.setPrimerNombre("Juan");
                admin.setSegundoNombre("Manuel");
                admin.setPrimerApellido("Villamil");
                admin.setSegundoApellido("Vargas");
                admin.setTelefono("3001234567");
                admin.setEstadoUsuario(true);
                admin.setRol(adminRolOpt.get());

                usuarioRepository.save(admin);

                System.out.println("✅ Usuario admin creado con éxito.");
                System.out.println("   Email: " + adminEmail);
                System.out.println("   Password: admin123");
            } else {
                System.err.println("❌ ERROR: Rol ADMINISTRADOR no existe.");
            }
            }
    }

    private void initializeEnvios() {
        if (envioRepository.count() == 0) {
            Optional<Usuario> adminOpt = usuarioRepository.findByEmail("admin@enviart.com");
            if (adminOpt.isPresent()) {
                Usuario admin = adminOpt.get();

                createEnvio(admin, "Bogotá", "Medellín", EstadoEnvio.ENTREGADO, new BigDecimal("15000"));
                createEnvio(admin, "Cali", "Bogotá", EstadoEnvio.EN_TRANSITO, new BigDecimal("12500"));
                createEnvio(admin, "Medellín", "Cartagena", EstadoEnvio.RECEPCIONADO, new BigDecimal("18000"));
                createEnvio(admin, "Bogotá", "Cali", EstadoEnvio.ENTREGADO, new BigDecimal("14000"));
                createEnvio(admin, "Barranquilla", "Bogotá", EstadoEnvio.EN_DISTRIBUCION, new BigDecimal("20000"));
                createEnvio(admin, "Cartagena", "Medellín", EstadoEnvio.RECEPCIONADO, new BigDecimal("16000"));
                createEnvio(admin, "Bogotá", "Barranquilla", EstadoEnvio.ENTREGADO, new BigDecimal("22000"));
                createEnvio(admin, "Medellín", "Bogotá", EstadoEnvio.EN_TRANSITO, new BigDecimal("13000"));
                createEnvio(admin, "Cali", "Medellín", EstadoEnvio.RECEPCIONADO, new BigDecimal("14500"));
                createEnvio(admin, "Bogotá", "Cali", EstadoEnvio.CANCELADO, new BigDecimal("12000"));

                System.out.println("✅ Envíos de prueba creados.");
            }
        }
    }

    private void createEnvio(Usuario usuario, String origen, String destino, EstadoEnvio estado, BigDecimal tarifa) {
        Envio envio = new Envio();
        envio.setRemitenteNombre("Usuario Prueba");
        envio.setRemitenteTelefono("3000000000");
        envio.setRemitenteDireccion("Calle Falsa 123");
        envio.setRemitenteCiudad(origen);

        envio.setDestinatarioNombre("Cliente Final");
        envio.setDestinatarioTelefono("3100000000");
        envio.setDestinatarioDireccion("Carrera 100 # 10-20");
        envio.setDestinatarioCiudad(destino);

        envio.setPesoKg(new BigDecimal("2.5"));
        envio.setTarifa(tarifa);
        envio.setEstado(estado);
        envio.setUsuarioRegistro(usuario);

        if (estado == EstadoEnvio.ENTREGADO) {
            envio.setFechaEntregaReal(java.time.LocalDateTime.now());
        }

        envioRepository.save(envio);
    }
    }

