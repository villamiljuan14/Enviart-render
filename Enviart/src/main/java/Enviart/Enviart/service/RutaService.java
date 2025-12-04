package Enviart.Enviart.service;

import Enviart.Enviart.model.Usuario;
import Enviart.Enviart.model.optimizada.Ruta;
import Enviart.Enviart.model.optimizada.Vehiculo;
import Enviart.Enviart.repository.RutaRepository;
import Enviart.Enviart.repository.UsuarioRepository;
import Enviart.Enviart.repository.VehiculoRepository;
import Enviart.Enviart.util.enums.TipoRol;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RutaService {

    private final RutaRepository rutaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;

    public RutaService(RutaRepository rutaRepository, VehiculoRepository vehiculoRepository,
            UsuarioRepository usuarioRepository) {
        this.rutaRepository = rutaRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Ruta> listarRutas() {
        return rutaRepository.findAll();
    }

    public Optional<Ruta> buscarPorId(Integer id) {
        return rutaRepository.findById(id);
    }

    @Transactional
    public Ruta guardarRuta(Ruta ruta) {
        // Validar que el vehículo existe
        if (ruta.getVehiculo() != null && ruta.getVehiculo().getIdVehiculo() != null) {
            vehiculoRepository.findById(ruta.getVehiculo().getIdVehiculo())
                    .orElseThrow(() -> new RuntimeException("El vehículo seleccionado no existe"));
        }

        // Validar que el conductor existe y es MENSAJERO
        if (ruta.getConductor() != null && ruta.getConductor().getIdUsuario() != null) {
            Usuario conductor = usuarioRepository.findById(ruta.getConductor().getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("El conductor seleccionado no existe"));

            if (conductor.getRol() == null || conductor.getRol().getTipoRol() != TipoRol.MENSAJERO) {
                throw new RuntimeException("El usuario seleccionado no es un mensajero");
            }
        }

        // Verificar si el nombre de ruta ya existe en otra ruta
        if (ruta.getIdRuta() == null) {
            // Nueva ruta
            if (rutaRepository.findByNombreRuta(ruta.getNombreRuta()).isPresent()) {
                throw new RuntimeException("El nombre de ruta ya está registrado");
            }
        } else {
            // Actualización de ruta
            rutaRepository.findByNombreRuta(ruta.getNombreRuta()).ifPresent(existente -> {
                if (!existente.getIdRuta().equals(ruta.getIdRuta())) {
                    throw new RuntimeException("El nombre de ruta ya está registrado por otra ruta");
                }
            });
        }

        return rutaRepository.save(ruta);
    }

    @Transactional
    public void eliminarRuta(Integer id) {
        // Verificar que la ruta existe antes de eliminarla
        rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

        // Nota: Las rutas generalmente no tienen dependencias directas en otras tablas
        // pero validamos su existencia antes de eliminar
        rutaRepository.deleteById(id);
    }

    public List<Vehiculo> listarVehiculos() {
        return vehiculoRepository.findAll();
    }

    public List<Usuario> listarConductores() {
        // Obtener solo usuarios con rol MENSAJERO
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() != null && u.getRol().getTipoRol() == TipoRol.MENSAJERO)
                .collect(Collectors.toList());
    }

    public List<Ruta> filtrarRutas(java.time.LocalDate fecha, String estado, String conductorEmail) {
        List<Ruta> rutas = rutaRepository.findAll();

        return rutas.stream()
                .filter(r -> {
                    boolean matches = true;

                    // Filtro por fecha inicio
                    if (fecha != null) {
                        if (r.getFechaInicio() == null || !r.getFechaInicio().equals(fecha)) {
                            matches = false;
                        }
                    }

                    // Filtro por estado
                    if (matches && estado != null && !estado.isEmpty()) {
                        if (r.getStatusRuta() == null || !r.getStatusRuta().equals(estado)) {
                            matches = false;
                        }
                    }

                    // Filtro por conductor (email)
                    if (matches && conductorEmail != null && !conductorEmail.isEmpty()) {
                        if (r.getConductor() == null
                                || !r.getConductor().getEmail().toLowerCase().contains(conductorEmail.toLowerCase())) {
                            matches = false;
                        }
                    }

                    return matches;
                })
                .collect(Collectors.toList());
    }
}
