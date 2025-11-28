package Enviart.Enviart.service;

import Enviart.Enviart.model.AuditoriaEnvio;
import Enviart.Enviart.model.Envio;
import Enviart.Enviart.model.Usuario;
import Enviart.Enviart.repository.AuditoriaEnvioRepository;
import Enviart.Enviart.repository.EnvioRepository;
import Enviart.Enviart.util.enums.EstadoEnvio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final AuditoriaEnvioRepository auditoriaRepository;

    public EnvioService(EnvioRepository envioRepository, AuditoriaEnvioRepository auditoriaRepository) {
        this.envioRepository = envioRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    /**
     * Registra un nuevo envío y crea el primer registro de auditoría
     */
    @Transactional
    public Envio registrarEnvio(Envio envio, Usuario usuarioRegistro) {
        // Calcular tarifa (simplificado - en producción usar lógica compleja)
        BigDecimal tarifa = calcularTarifa(envio);
        envio.setTarifa(tarifa);
        envio.setUsuarioRegistro(usuarioRegistro);
        envio.setEstado(EstadoEnvio.RECEPCIONADO);

        // Guardar envío
        Envio envioGuardado = envioRepository.save(envio);

        // Crear registro de auditoría
        registrarCambioEstado(envioGuardado, null, EstadoEnvio.RECEPCIONADO, usuarioRegistro,
                "Envío registrado en el sistema", null, null);

        return envioGuardado;
    }

    /**
     * Cambia el estado de un envío y registra en auditoría
     */
    @Transactional
    public Envio cambiarEstado(Integer idEnvio, EstadoEnvio nuevoEstado, Usuario usuario,
            String observaciones, Double latitud, Double longitud) {
        Envio envio = envioRepository.findById(idEnvio)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado"));

        EstadoEnvio estadoAnterior = envio.getEstado();

        // Validar transición de estado
        validarTransicionEstado(estadoAnterior, nuevoEstado);

        envio.setEstado(nuevoEstado);

        // Si se entregó, registrar fecha
        if (nuevoEstado == EstadoEnvio.ENTREGADO) {
            envio.setFechaEntregaReal(LocalDateTime.now());
        }

        Envio envioActualizado = envioRepository.save(envio);

        // Registrar en auditoría
        registrarCambioEstado(envioActualizado, estadoAnterior, nuevoEstado, usuario,
                observaciones, latitud, longitud);

        return envioActualizado;
    }

    /**
     * Asigna un transportista a un envío
     */
    @Transactional
    public Envio asignarTransportista(Integer idEnvio, Usuario transportista, Usuario usuarioAsigna) {
        Envio envio = envioRepository.findById(idEnvio)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado"));

        envio.setTransportista(transportista);
        Envio envioActualizado = envioRepository.save(envio);

        // Registrar en auditoría
        registrarCambioEstado(envioActualizado, envio.getEstado(), envio.getEstado(), usuarioAsigna,
                "Transportista asignado: " + transportista.getPrimerNombre() + " " +
                        transportista.getPrimerApellido(),
                null, null);

        return envioActualizado;
    }

    /**
     * Busca envíos asignados a un transportista específico
     */
    public List<Envio> buscarPorTransportista(Usuario transportista) {
        return envioRepository.findByTransportista(transportista);
    }

    /**
     * Busca envío por número de guía
     */
    public Optional<Envio> buscarPorNumeroGuia(String numeroGuia) {
        return envioRepository.findByNumeroGuia(numeroGuia);
    }

    /**
     * Lista todos los envíos
     */
    public List<Envio> listarTodos() {
        return envioRepository.findAll();
    }

    /**
     * Obtiene el historial de auditoría de un envío
     */
    public List<AuditoriaEnvio> obtenerHistorial(Integer idEnvio) {
        return auditoriaRepository.findByEnvio_IdEnvioOrderByFechaCambioDesc(idEnvio);
    }

    /**
     * Calcula la tarifa del envío (simplificado)
     */
    private BigDecimal calcularTarifa(Envio envio) {
        // Tarifa base
        BigDecimal tarifaBase = new BigDecimal("10.00");

        // Costo por peso (2.00 por kg)
        BigDecimal costoPeso = envio.getPesoKg().multiply(new BigDecimal("2.00"));

        // Costo por volumen si está disponible
        BigDecimal costoVolumen = BigDecimal.ZERO;
        if (envio.getLargoCm() != null && envio.getAnchoCm() != null && envio.getAltoCm() != null) {
            BigDecimal volumen = envio.getLargoCm()
                    .multiply(envio.getAnchoCm())
                    .multiply(envio.getAltoCm())
                    .divide(new BigDecimal("1000")); // Convertir a litros
            costoVolumen = volumen.multiply(new BigDecimal("0.50"));
        }

        return tarifaBase.add(costoPeso).add(costoVolumen);
    }

    /**
     * Valida que la transición de estado sea válida
     */
    private void validarTransicionEstado(EstadoEnvio estadoActual, EstadoEnvio nuevoEstado) {
        // Reglas de negocio para transiciones válidas
        if (estadoActual == EstadoEnvio.ENTREGADO || estadoActual == EstadoEnvio.CANCELADO) {
            throw new RuntimeException("No se puede cambiar el estado de un envío entregado o cancelado");
        }

        if (estadoActual == EstadoEnvio.RECEPCIONADO && nuevoEstado == EstadoEnvio.ENTREGADO) {
            throw new RuntimeException("Un envío recepcionado no puede pasar directamente a entregado");
        }
    }

    /**
     * Registra un cambio de estado en la tabla de auditoría
     */
    private void registrarCambioEstado(Envio envio, EstadoEnvio estadoAnterior, EstadoEnvio estadoNuevo,
            Usuario usuario, String observaciones, Double latitud, Double longitud) {
        AuditoriaEnvio auditoria = new AuditoriaEnvio();
        auditoria.setEnvio(envio);
        auditoria.setEstadoAnterior(estadoAnterior);
        auditoria.setEstadoNuevo(estadoNuevo);
        auditoria.setUsuario(usuario);
        auditoria.setObservaciones(observaciones);
        auditoria.setLatitud(latitud);
        auditoria.setLongitud(longitud);

        auditoriaRepository.save(auditoria);
    }
}
