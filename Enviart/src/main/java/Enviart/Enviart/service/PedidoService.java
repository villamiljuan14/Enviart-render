package Enviart.Enviart.service;

import Enviart.Enviart.dto.CrearPedidoDTO;
import Enviart.Enviart.dto.DireccionDTO;
import Enviart.Enviart.dto.PaqueteDTO;
import Enviart.Enviart.model.Usuario;
import Enviart.Enviart.model.optimizada.*;
import Enviart.Enviart.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PaqueteRepository paqueteRepository;

    @Autowired
    private DireccionRepository direccionRepository;

    @Autowired
    private TipoServicioRepository tipoServicioRepository;

    @Autowired
    private EstadoPedidoRepository estadoPedidoRepository;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Crear un nuevo pedido completo con paquetes y direcciones
     */
    @Transactional
    public Pedido crearPedido(CrearPedidoDTO pedidoDTO, String emailUsuario) {
        // Obtener el usuario autenticado
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Procesar dirección de origen
        Direccion direccionOrigen = procesarDireccion(pedidoDTO.getDireccionOrigen(), usuario);

        // Procesar dirección de destino
        Direccion direccionDestino = procesarDireccion(pedidoDTO.getDireccionDestino(), usuario);

        // Obtener estado inicial del pedido (RECEPCIONADO)
        EstadoPedido estadoPendiente = estadoPedidoRepository.findByNombre("RECEPCIONADO")
                .orElseGet(() -> {
                    EstadoPedido nuevoEstado = new EstadoPedido();
                    nuevoEstado.setNombre("RECEPCIONADO");
                    return estadoPedidoRepository.save(nuevoEstado);
                });

        // Calcular el total del pedido
        BigDecimal totalFinal = calcularTotalPedido(pedidoDTO.getPaquetes());

        // Crear el pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setDireccionOrigen(direccionOrigen);
        pedido.setDireccionDestino(direccionDestino);
        pedido.setTotalFinal(totalFinal);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado(estadoPendiente);

        // Guardar el pedido
        pedido = pedidoRepository.save(pedido);

        // Crear los paquetes asociados al pedido
        for (PaqueteDTO paqueteDTO : pedidoDTO.getPaquetes()) {
            crearPaquete(paqueteDTO, pedido);
        }

        return pedido;
    }

    /**
     * Procesar dirección (crear nueva o usar existente)
     */
    private Direccion procesarDireccion(DireccionDTO direccionDTO, Usuario usuario) {
        // Si se proporciona un ID, usar dirección existente
        if (direccionDTO.getIdDireccion() != null) {
            return direccionRepository.findById(direccionDTO.getIdDireccion())
                    .orElseThrow(() -> new RuntimeException("Dirección no encontrada"));
        }

        // Crear nueva dirección
        Direccion direccion = new Direccion();
        direccion.setUsuario(usuario);
        direccion.setNombreContacto(direccionDTO.getNombreContacto());
        direccion.setTelefonoContacto(direccionDTO.getTelefonoContacto());
        direccion.setCalle(direccionDTO.getCalle());
        direccion.setNumero(direccionDTO.getNumero());
        direccion.setCodigoPostal(direccionDTO.getCodigoPostal());

        return direccionRepository.save(direccion);
    }

    /**
     * Crear un paquete asociado a un pedido
     */
    private Paquete crearPaquete(PaqueteDTO paqueteDTO, Pedido pedido) {
        TipoServicio tipoServicio = tipoServicioRepository.findById(paqueteDTO.getTipoServicioId())
                .orElseThrow(() -> new RuntimeException("Tipo de servicio no encontrado"));

        // Calcular costo del servicio basado en peso y dimensiones
        BigDecimal costoServicio = calcularCostoServicio(paqueteDTO, tipoServicio);

        Paquete paquete = new Paquete();
        paquete.setPedido(pedido);
        paquete.setTipoServicio(tipoServicio);
        paquete.setPesoKg(paqueteDTO.getPesoKg());
        paquete.setLargoCm(paqueteDTO.getLargoCm());
        paquete.setAnchoCm(paqueteDTO.getAnchoCm());
        paquete.setAltoCm(paqueteDTO.getAltoCm());
        paquete.setValorDeclarado(paqueteDTO.getValorDeclarado());
        paquete.setDescripcionContenido(paqueteDTO.getDescripcionContenido());
        paquete.setCostoServicio(costoServicio);

        return paqueteRepository.save(paquete);
    }

    /**
     * Calcular el costo del servicio para un paquete
     */
    private BigDecimal calcularCostoServicio(PaqueteDTO paqueteDTO, TipoServicio tipoServicio) {
        BigDecimal costoBase = tipoServicio.getCostoBase();

        // Calcular volumen en m³ (largo * ancho * alto / 1,000,000)
        BigDecimal volumen = paqueteDTO.getLargoCm()
                .multiply(paqueteDTO.getAnchoCm())
                .multiply(paqueteDTO.getAltoCm())
                .divide(new BigDecimal("1000000"), 6, RoundingMode.HALF_UP);

        // Factor de peso volumétrico (kg/m³)
        BigDecimal pesoVolumetrico = volumen.multiply(new BigDecimal("200"));

        // Usar el mayor entre peso real y peso volumétrico
        BigDecimal pesoFacturable = paqueteDTO.getPesoKg().max(pesoVolumetrico);

        // Costo = costo base + (peso facturable * factor)
        BigDecimal factor = new BigDecimal("2.5");
        BigDecimal costoTotal = costoBase.add(pesoFacturable.multiply(factor));

        // Redondear a 2 decimales
        return costoTotal.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcular el total del pedido sumando todos los paquetes
     */
    private BigDecimal calcularTotalPedido(List<PaqueteDTO> paquetes) {
        BigDecimal total = BigDecimal.ZERO;

        for (PaqueteDTO paqueteDTO : paquetes) {
            TipoServicio tipoServicio = tipoServicioRepository.findById(paqueteDTO.getTipoServicioId())
                    .orElseThrow(() -> new RuntimeException("Tipo de servicio no encontrado"));
            BigDecimal costo = calcularCostoServicio(paqueteDTO, tipoServicio);
            total = total.add(costo);
        }

        return total;
    }

    /**
     * Obtener todos los pedidos de un usuario
     */
    public List<Pedido> obtenerPedidosPorUsuario(String emailUsuario) {
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        System.out.println("DEBUG [obtenerPedidosPorUsuario]: Buscando pedidos para usuario ID=" +
                usuario.getIdUsuario() + ", email=" + usuario.getEmail());
        List<Pedido> pedidos = pedidoRepository.findByUsuarioOrderByFechaPedidoDesc(usuario);
        System.out.println("DEBUG [obtenerPedidosPorUsuario]: Encontrados " + pedidos.size() + " pedidos");
        return pedidos;
    }

    /**
     * Obtener un pedido por ID
     */
    public Optional<Pedido> obtenerPedidoPorId(Integer id) {
        return pedidoRepository.findById(id);
    }

    /**
     * Obtener todos los pedidos
     */
    public List<Pedido> obtenerTodosPedidos() {
        return pedidoRepository.findAll();
    }

    /**
     * Obtener pedidos por estado
     */
    public List<Pedido> obtenerPedidosPorEstado(String nombreEstado) {
        return pedidoRepository.findByEstadoNombre(nombreEstado);
    }

    /**
     * Actualizar estado de un pedido
     */
    @Transactional
    public Pedido actualizarEstadoPedido(Integer pedidoId, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        EstadoPedido estado = estadoPedidoRepository.findByNombre(nuevoEstado)
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"));

        pedido.setEstado(estado);
        return pedidoRepository.save(pedido);
    }

    /**
     * Cancelar un pedido
     */
    @Transactional
    public Pedido cancelarPedido(Integer pedidoId) {
        return actualizarEstadoPedido(pedidoId, "CANCELADO");
    }

    /**
     * Obtener paquetes de un pedido
     */
    public List<Paquete> obtenerPaquetesPorPedido(Integer pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        return paqueteRepository.findByPedido(pedido);
    }

    /**
     * Obtener direcciones de un usuario
     */
    public List<Direccion> obtenerDireccionesPorUsuario(String emailUsuario) {
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return direccionRepository.findByUsuario(usuario);
    }

    /**
     * Obtener todos los tipos de servicio disponibles
     */
    public List<TipoServicio> obtenerTiposServicio() {
        return tipoServicioRepository.findAll();
    }

    /**
     * Obtener todos los estados de pedido
     */
    public List<EstadoPedido> obtenerEstadosPedido() {
        return estadoPedidoRepository.findAll();
    }

    /**
     * Filtrar pedidos por criterios
     */
    public List<Pedido> filtrarPedidos(java.time.LocalDate desde, java.time.LocalDate hasta, String estado,
            String usuarioEmail) {
        List<Pedido> pedidos = pedidoRepository.findAll();

        return pedidos.stream()
                .filter(p -> {
                    boolean matches = true;

                    // Filtro por fecha desde
                    if (desde != null) {
                        if (p.getFechaPedido().toLocalDate().isBefore(desde)) {
                            matches = false;
                        }
                    }

                    // Filtro por fecha hasta
                    if (matches && hasta != null) {
                        if (p.getFechaPedido().toLocalDate().isAfter(hasta)) {
                            matches = false;
                        }
                    }

                    // Filtro por estado
                    if (matches && estado != null && !estado.isEmpty()) {
                        if (p.getEstado() == null || !p.getEstado().getNombre().equals(estado)) {
                            matches = false;
                        }
                    }

                    // Filtro por email de usuario
                    if (matches && usuarioEmail != null && !usuarioEmail.isEmpty()) {
                        if (p.getUsuario() == null
                                || !p.getUsuario().getEmail().toLowerCase().contains(usuarioEmail.toLowerCase())) {
                            matches = false;
                        }
                    }

                    return matches;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Contar todos los pedidos en el sistema
     */
    public long contarTodosLosPedidos() {
        return pedidoRepository.count();
    }

    /**
     * Contar pedidos por estado
     */
    public long contarPedidosPorEstado(String nombreEstado) {
        return pedidoRepository.findByEstadoNombre(nombreEstado).size();
    }

    /**
     * Obtener pedidos por rango de fecha
     */
    public List<Pedido> obtenerPedidosPorRangoFecha(LocalDateTime inicio, LocalDateTime fin) {
        return pedidoRepository.findByFechaPedidoBetween(inicio, fin);
    }

    /**
     * Obtener pedidos asignados a un conductor (a través de rutas)
     */
    public List<Pedido> obtenerPedidosPorConductor(Integer conductorId) {
        return pedidoRepository.findPedidosByConductorId(conductorId);
    }
}
