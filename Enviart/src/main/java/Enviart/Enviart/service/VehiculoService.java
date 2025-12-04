package Enviart.Enviart.service;

import Enviart.Enviart.exception.ReferentialIntegrityException;
import Enviart.Enviart.model.optimizada.Vehiculo;
import Enviart.Enviart.repository.RutaRepository;
import Enviart.Enviart.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final RutaRepository rutaRepository;

    @Autowired
    public VehiculoService(VehiculoRepository vehiculoRepository, RutaRepository rutaRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.rutaRepository = rutaRepository;
    }

    public List<Vehiculo> listarVehiculos() {
        return vehiculoRepository.findAll();
    }

    public Optional<Vehiculo> buscarPorId(Integer id) {
        return vehiculoRepository.findById(id);
    }

    @Transactional
    public Vehiculo guardarVehiculo(Vehiculo vehiculo) {
        // Verificar si la placa ya existe en otro vehículo
        if (vehiculo.getIdVehiculo() == null) {
            if (vehiculoRepository.findByPlaca(vehiculo.getPlaca()).isPresent()) {
                throw new RuntimeException("La placa ya está registrada");
            }
        } else {
            vehiculoRepository.findByPlaca(vehiculo.getPlaca()).ifPresent(existente -> {
                if (!existente.getIdVehiculo().equals(vehiculo.getIdVehiculo())) {
                    throw new RuntimeException("La placa ya está registrada por otro vehículo");
                }
            });
        }
        return vehiculoRepository.save(vehiculo);
    }

    @Transactional
    public void eliminarVehiculo(Integer id) {
        // 1. Verificar que el vehículo existe
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        // 2. Verificar si tiene rutas asignadas
        long rutasAsignadas = rutaRepository.countByVehiculo_IdVehiculo(id);

        if (rutasAsignadas > 0) {
            throw new ReferentialIntegrityException(
                    String.format(
                            "No se puede eliminar el vehículo con placa %s porque tiene %d ruta(s) asignada(s). " +
                                    "Por favor, elimine o reasigne las rutas antes de eliminar el vehículo.",
                            vehiculo.getPlaca(), rutasAsignadas));
        }

        // 3. Si no tiene dependencias, proceder con la eliminación
        vehiculoRepository.deleteById(id);
    }

    public List<Vehiculo> filtrarVehiculos(String placa, String modelo) {
        List<Vehiculo> vehiculos = vehiculoRepository.findAll();

        return vehiculos.stream()
                .filter(v -> {
                    boolean matches = true;

                    // Filtro por placa
                    if (placa != null && !placa.isEmpty()) {
                        if (v.getPlaca() == null || !v.getPlaca().toLowerCase().contains(placa.toLowerCase())) {
                            matches = false;
                        }
                    }

                    // Filtro por modelo
                    if (matches && modelo != null && !modelo.isEmpty()) {
                        if (v.getModelo() == null || !v.getModelo().toLowerCase().contains(modelo.toLowerCase())) {
                            matches = false;
                        }
                    }

                    return matches;
                })
                .collect(java.util.stream.Collectors.toList());
    }
}
