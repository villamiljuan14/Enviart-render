package Enviart.Enviart.service;

import Enviart.Enviart.model.optimizada.Vehiculo;
import Enviart.Enviart.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;

    @Autowired
    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
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
        vehiculoRepository.deleteById(id);
    }
}
