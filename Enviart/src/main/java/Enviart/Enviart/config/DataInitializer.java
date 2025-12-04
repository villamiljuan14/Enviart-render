package Enviart.Enviart.config;

import Enviart.Enviart.model.Envio;
import Enviart.Enviart.model.Rol;
import Enviart.Enviart.model.Usuario;
import Enviart.Enviart.model.optimizada.EstadoPedido;
import Enviart.Enviart.model.optimizada.TipoServicio;
import Enviart.Enviart.model.optimizada.TrackingLocation;
import Enviart.Enviart.repository.*;
import Enviart.Enviart.util.enums.EstadoEnvio;
import Enviart.Enviart.util.enums.TipoDocumento;
import Enviart.Enviart.util.enums.TipoRol;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final EnvioRepository envioRepository;
    private final TipoServicioRepository tipoServicioRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final TrackingLocationRepository trackingLocationRepository;

    public DataInitializer(PasswordEncoder passwordEncoder, RolRepository rolRepository,
            UsuarioRepository usuarioRepository, EnvioRepository envioRepository,
            TipoServicioRepository tipoServicioRepository, EstadoPedidoRepository estadoPedidoRepository,
            TrackingLocationRepository trackingLocationRepository) {
        this.passwordEncoder = passwordEncoder;
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.envioRepository = envioRepository;
        this.tipoServicioRepository = tipoServicioRepository;
        this.estadoPedidoRepository = estadoPedidoRepository;
        this.trackingLocationRepository = trackingLocationRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Crear roles si no existen
        initializeRoles(TipoRol.ADMINISTRADOR);
        initializeRoles(TipoRol.CLIENTE);
        initializeRoles(TipoRol.MENSAJERO);
        initializeRoles(TipoRol.PROVEEDOR);

        // Crear usuarios por defecto para cada rol
        initializeAdminUser();
        initializeProveedorUser();
        initializeClienteUser();
        initializeMensajeroUser();
        initializeTipoServicios();
        initializeEstadoPedidos();

        // Crear envíos de prueba
        initializeEnvios();

        // Crear datos de tracking GPS para pedidos en tránsito
        initializeTrackingData();
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

    private void initializeProveedorUser() {
        String provEmail = "proveedor@enviart.com";
        Optional<Usuario> userOpt = usuarioRepository.findByEmail(provEmail);

        if (userOpt.isEmpty()) {
            Optional<Rol> provRolOpt = rolRepository.findByTipoRol(TipoRol.PROVEEDOR);

            if (provRolOpt.isPresent()) {
                Usuario proveedor = new Usuario();
                proveedor.setEmail(provEmail);
                proveedor.setContrasenaUsuario(passwordEncoder.encode("proveedor123"));
                proveedor.setDocUsuario("1000500258");
                proveedor.setTipoDocumento(TipoDocumento.CC);
                proveedor.setPrimerNombre("Jose");
                proveedor.setSegundoNombre("Andres");
                proveedor.setPrimerApellido("Garcia");
                proveedor.setSegundoApellido("Garcia");
                proveedor.setTelefono("3109876543");
                proveedor.setEstadoUsuario(true);
                proveedor.setRol(provRolOpt.get());

                usuarioRepository.save(proveedor);

                System.out.println("✅ Usuario proveedor creado con éxito.");
                System.out.println("   Email: " + provEmail);
                System.out.println("   Password: proveedor123");
            }
        }
    }

    private void initializeClienteUser() {
        String clienteEmail = "cliente@enviart.com";
        Optional<Usuario> userOpt = usuarioRepository.findByEmail(clienteEmail);

        if (userOpt.isEmpty()) {
            Optional<Rol> clienteRolOpt = rolRepository.findByTipoRol(TipoRol.CLIENTE);

            if (clienteRolOpt.isPresent()) {
                Usuario cliente = new Usuario();
                cliente.setEmail(clienteEmail);
                cliente.setContrasenaUsuario(passwordEncoder.encode("cliente123"));
                cliente.setDocUsuario("1000600300");
                cliente.setTipoDocumento(TipoDocumento.CC);
                cliente.setPrimerNombre("Maria");
                cliente.setSegundoNombre("Fernanda");
                cliente.setPrimerApellido("Lopez");
                cliente.setSegundoApellido("Martinez");
                cliente.setTelefono("3201234567");
                cliente.setEstadoUsuario(true);
                cliente.setRol(clienteRolOpt.get());

                usuarioRepository.save(cliente);

                System.out.println("✅ Usuario cliente creado con éxito.");
                System.out.println("   Email: " + clienteEmail);
                System.out.println("   Password: cliente123");
            } else {
                System.err.println("❌ ERROR: Rol CLIENTE no existe.");
            }
        }
    }

    private void initializeMensajeroUser() {
        String mensajeroEmail = "mensajero@enviart.com";
        Optional<Usuario> userOpt = usuarioRepository.findByEmail(mensajeroEmail);

        if (userOpt.isEmpty()) {
            Optional<Rol> mensajeroRolOpt = rolRepository.findByTipoRol(TipoRol.MENSAJERO);

            if (mensajeroRolOpt.isPresent()) {
                Usuario mensajero = new Usuario();
                mensajero.setEmail(mensajeroEmail);
                mensajero.setContrasenaUsuario(passwordEncoder.encode("mensajero123"));
                mensajero.setDocUsuario("1000700400");
                mensajero.setTipoDocumento(TipoDocumento.CC);
                mensajero.setPrimerNombre("Carlos");
                mensajero.setSegundoNombre("Alberto");
                mensajero.setPrimerApellido("Rodriguez");
                mensajero.setSegundoApellido("Perez");
                mensajero.setTelefono("3151234567");
                mensajero.setEstadoUsuario(true);
                mensajero.setRol(mensajeroRolOpt.get());

                usuarioRepository.save(mensajero);

                System.out.println("✅ Usuario mensajero creado con éxito.");
                System.out.println("   Email: " + mensajeroEmail);
                System.out.println("   Password: mensajero123");
            } else {
                System.err.println("❌ ERROR: Rol MENSAJERO no existe.");
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

    private void initializeTipoServicios() {
        if (tipoServicioRepository.count() == 0) {
            TipoServicio estandar = new TipoServicio();
            estandar.setNombre("Estándar");
            estandar.setCostoBase(new java.math.BigDecimal("15.00"));
            estandar.setPesoMax(new java.math.BigDecimal("30.00"));
            estandar.setTiempoEntregaEst("3-5 días hábiles");

            TipoServicio express = new TipoServicio();
            express.setNombre("Express");
            express.setCostoBase(new java.math.BigDecimal("30.00"));
            express.setPesoMax(new java.math.BigDecimal("20.00"));
            express.setTiempoEntregaEst("1-2 días hábiles");

            TipoServicio sameDay = new TipoServicio();
            sameDay.setNombre("Same Day");
            sameDay.setCostoBase(new java.math.BigDecimal("50.00"));
            sameDay.setPesoMax(new java.math.BigDecimal("10.00"));
            sameDay.setTiempoEntregaEst("Mismo día");

            TipoServicio economico = new TipoServicio();
            economico.setNombre("Económico");
            economico.setCostoBase(new java.math.BigDecimal("10.00"));
            economico.setPesoMax(new java.math.BigDecimal("50.00"));
            economico.setTiempoEntregaEst("5-7 días hábiles");

            TipoServicio premium = new TipoServicio();
            premium.setNombre("Premium");
            premium.setCostoBase(new java.math.BigDecimal("75.00"));
            premium.setPesoMax(new java.math.BigDecimal("15.00"));
            premium.setTiempoEntregaEst("24 horas");

            tipoServicioRepository.saveAll(java.util.List.of(estandar, express, sameDay, economico, premium));
            System.out.println("✅ Tipos de servicio de envío inicializados.");
        }
    }

    private void initializeEstadoPedidos() {
        // Inicializar Estados de Pedido asegurando que todos existan
        Arrays.stream(EstadoEnvio.values()).forEach(estadoEnum -> {
            if (estadoPedidoRepository.findByNombre(estadoEnum.name()).isEmpty()) {
                EstadoPedido estado = new EstadoPedido();
                estado.setNombre(estadoEnum.name());
                estado.setOrden(estadoEnum.ordinal());
                estadoPedidoRepository.save(estado);
                System.out.println("✅ Estado creado: " + estadoEnum.name());
            }
        });
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

    private void initializeTrackingData() {
        // Obtener pedidos EN_TRANSITO para agregarles tracking
        envioRepository.findAll().stream()
                .filter(envio -> envio.getEstado() == EstadoEnvio.EN_TRANSITO)
                .filter(envio -> trackingLocationRepository.countByPedido_IdEnvio(envio.getIdEnvio()) == 0)
                .limit(3) // Máximo 3 pedidos
                .forEach(envio -> {
                    // Coordenadas de Bogotá (centro aproximado: 4.7110, -74.0721)
                    // Crear una ruta simulada con 5 puntos GPS
                    LocalDateTime now = LocalDateTime.now();

                    // Punto 1: hace 2 horas - Inicio del recorrido
                    createTrackingPoint(envio, 4.6800, -74.0500, 0, now.minusHours(2));

                    // Punto 2: hace 90 min - En movimiento
                    createTrackingPoint(envio, 4.6900, -74.0600, 35, now.minusMinutes(90));

                    // Punto 3: hace 1 hora - Velocidad estable
                    createTrackingPoint(envio, 4.7000, -74.0680, 42, now.minusMinutes(60));

                    // Punto 4: hace 30 min - Acercándose al destino
                    createTrackingPoint(envio, 4.7050, -74.0700, 38, now.minusMinutes(30));

                    // Punto 5: hace 5 min - Posición actual
                    createTrackingPoint(envio, 4.7110, -74.0721, 25, now.minusMinutes(5));

                    System.out.println("✅ Tracking GPS creado para pedido: " + envio.getNumeroGuia());
                });
    }

    private void createTrackingPoint(Envio envio, double lat, double lon, double speed, LocalDateTime time) {
        TrackingLocation loc = TrackingLocation.builder()
                .pedido(envio)
                .latitud(new BigDecimal(lat))
                .longitud(new BigDecimal(lon))
                .velocidad(new BigDecimal(speed))
                .timestamp(time)
                .build();
        trackingLocationRepository.save(loc);
    }
}
