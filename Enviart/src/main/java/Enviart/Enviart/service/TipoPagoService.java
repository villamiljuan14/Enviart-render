package Enviart.Enviart.service;

import Enviart.Enviart.exception.ReferentialIntegrityException;
import Enviart.Enviart.model.optimizada.TipoPago;
import Enviart.Enviart.repository.PagoRepository;
import Enviart.Enviart.repository.TipoPagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TipoPagoService {

    private final TipoPagoRepository tipoPagoRepository;
    private final PagoRepository pagoRepository;

    public TipoPagoService(TipoPagoRepository tipoPagoRepository, PagoRepository pagoRepository) {
        this.tipoPagoRepository = tipoPagoRepository;
        this.pagoRepository = pagoRepository;
    }

    public List<TipoPago> listarTiposPago() {
        return tipoPagoRepository.findAll();
    }

    public Optional<TipoPago> buscarPorId(Integer id) {
        return tipoPagoRepository.findById(id);
    }

    @Transactional
    public TipoPago guardarTipoPago(TipoPago tipoPago) {
        // Verificar si el nombre ya existe
        if (tipoPago.getIdTipoPago() == null) {
            // Nuevo tipo de pago
            if (tipoPagoRepository.findByNombre(tipoPago.getNombre()).isPresent()) {
                throw new RuntimeException("El nombre del tipo de pago ya está registrado");
            }
        } else {
            // Actualización
            tipoPagoRepository.findByNombre(tipoPago.getNombre()).ifPresent(existente -> {
                if (!existente.getIdTipoPago().equals(tipoPago.getIdTipoPago())) {
                    throw new RuntimeException("El nombre del tipo de pago ya está registrado por otro tipo");
                }
            });
        }

        return tipoPagoRepository.save(tipoPago);
    }

    @Transactional
    public void eliminarTipoPago(Integer id) {
        // 1. Verificar que el tipo de pago existe
        TipoPago tipoPago = tipoPagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de pago no encontrado"));

        // 2. Verificar si está siendo usado en pagos
        long pagosUsandoTipo = pagoRepository.countByTipoPago_IdTipoPago(id);

        if (pagosUsandoTipo > 0) {
            throw new ReferentialIntegrityException(
                    String.format("No se puede eliminar el tipo de pago '%s' porque está siendo usado en %d pago(s). " +
                            "No es posible eliminar tipos de pago con historial de uso.",
                            tipoPago.getNombre(), pagosUsandoTipo));
        }

        // 3. Si no tiene dependencias, proceder con la eliminación
        tipoPagoRepository.deleteById(id);
    }
}
