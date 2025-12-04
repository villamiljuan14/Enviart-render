package Enviart.Enviart.service;

import Enviart.Enviart.model.optimizada.TipoServicio;
import Enviart.Enviart.repository.TipoServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TipoServicioService {

    private final TipoServicioRepository tipoServicioRepository;

    @Autowired
    public TipoServicioService(TipoServicioRepository tipoServicioRepository) {
        this.tipoServicioRepository = tipoServicioRepository;
    }

    public List<TipoServicio> listarTiposServicio() {
        return tipoServicioRepository.findAll();
    }

    public Optional<TipoServicio> buscarPorId(Integer id) {
        return tipoServicioRepository.findById(id);
    }

    @Transactional
    public TipoServicio guardarTipoServicio(TipoServicio tipoServicio) {
        return tipoServicioRepository.save(tipoServicio);
    }

    @Transactional
    public void eliminarTipoServicio(Integer id) {
        // Verificar que el tipo de servicio existe antes de eliminarlo
        tipoServicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de servicio no encontrado"));

        // Nota: Validar si TipoServicio tiene dependencias depende del modelo de datos
        // Actualmente no se encontraron referencias FK, pero se debe revisar
        tipoServicioRepository.deleteById(id);
    }
}
