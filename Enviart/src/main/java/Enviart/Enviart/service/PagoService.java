package Enviart.Enviart.service;

import Enviart.Enviart.model.optimizada.Pago;
import Enviart.Enviart.model.optimizada.Pedido;
import Enviart.Enviart.model.optimizada.TipoPago;
import Enviart.Enviart.repository.PagoRepository;
import Enviart.Enviart.repository.PedidoRepository;
import Enviart.Enviart.repository.TipoPagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final TipoPagoRepository tipoPagoRepository;

    public PagoService(PagoRepository pagoRepository, PedidoRepository pedidoRepository,
            TipoPagoRepository tipoPagoRepository) {
        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.tipoPagoRepository = tipoPagoRepository;
    }

    public List<Pago> listarPagos() {
        return pagoRepository.findAll();
    }

    public Optional<Pago> buscarPorId(Integer id) {
        return pagoRepository.findById(id);
    }

    public List<Pago> listarPagosPorPedido(Integer idPedido) {
        return pedidoRepository.findById(idPedido)
                .map(pagoRepository::findByPedidoOrderByFechaPagoDesc)
                .orElse(List.of());
    }

    @Transactional
    public Pago guardarPago(Pago pago) {
        // Validar que el pedido existe
        if (pago.getPedido() != null && pago.getPedido().getIdPedido() != null) {
            pedidoRepository.findById(pago.getPedido().getIdPedido())
                    .orElseThrow(() -> new RuntimeException("El pedido seleccionado no existe"));
        }

        // Validar que el tipo de pago existe
        if (pago.getTipoPago() != null && pago.getTipoPago().getIdTipoPago() != null) {
            tipoPagoRepository.findById(pago.getTipoPago().getIdTipoPago())
                    .orElseThrow(() -> new RuntimeException("El tipo de pago seleccionado no existe"));
        }

        // Validar que el monto sea positivo
        if (pago.getMonto() != null && pago.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("El monto debe ser mayor a 0");
        }

        return pagoRepository.save(pago);
    }

    @Transactional
    public void eliminarPago(Integer id) {
        // Verificar que el pago existe antes de eliminarlo
        pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));

        // Nota: Los pagos generalmente no tienen dependencias (no son referenciados por
        // otras tablas)
        // pero validamos su existencia antes de eliminar
        pagoRepository.deleteById(id);
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public List<TipoPago> listarTiposPago() {
        return tipoPagoRepository.findAll();
    }
}
