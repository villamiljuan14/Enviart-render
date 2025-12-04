package Enviart.Enviart.exception;

/**
 * Excepción lanzada cuando se viola una restricción de integridad referencial
 * al intentar eliminar una entidad que tiene dependencias.
 */
public class ReferentialIntegrityException extends RuntimeException {

    public ReferentialIntegrityException(String message) {
        super(message);
    }

    public ReferentialIntegrityException(String message, Throwable cause) {
        super(message, cause);
    }
}
