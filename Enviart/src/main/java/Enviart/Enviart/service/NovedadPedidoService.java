package Enviart.Enviart.service;

import Enviart.Enviart.model.optimizada.NovedadPedido;
import Enviart.Enviart.model.optimizada.Pedido;
import Enviart.Enviart.repository.NovedadPedidoRepository;
import Enviart.Enviart.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NovedadPedidoService {

    private final NovedadPedidoRepository novedadRepository;
    private final PedidoRepository pedidoRepository;

    public NovedadPedidoService(NovedadPedidoRepository novedadRepository, PedidoRepository pedidoRepository) {
        this.novedadRepository = novedadRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public List<NovedadPedido> listarNovedades() {
        return novedadRepository.findAllByOrderByFechaNovedadDesc();
    }

    public Optional<NovedadPedido> buscarPorId(Integer id) {
        return novedadRepository.findById(id);
    }

    public List<NovedadPedido> listarNovedadesPorPedido(Integer idPedido) {
        return pedidoRepository.findById(idPedido)
                .map(novedadRepository::findByPedidoOrderByFechaNovedadDesc)
                .orElse(List.of());
    }

    @Transactional
    public NovedadPedido guardarNovedad(NovedadPedido novedad) {
        // Validar que el pedido existe
        if (novedad.getPedido() != null && novedad.getPedido().getIdPedido() != null) {
            pedidoRepository.findById(novedad.getPedido().getIdPedido())
                    .orElseThrow(() -> new RuntimeException("El pedido seleccionado no existe"));
        }

        return novedadRepository.save(novedad);
    }

    @Transactional
    public void eliminarNovedad(Integer id) {
        novedadRepository.deleteById(id);
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }
}
