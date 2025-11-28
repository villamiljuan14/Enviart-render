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
        tipoServicioRepository.deleteById(id);
    }
}
