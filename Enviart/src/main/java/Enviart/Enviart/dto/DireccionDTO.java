package Enviart.Enviart.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionDTO {

    private Integer idDireccion; // Opcional, para usar direcciones existentes

    @NotBlank(message = "El nombre del contacto es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombreContacto;

    @NotBlank(message = "El teléfono del contacto es requerido")
    @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe tener 10 dígitos")
    private String telefonoContacto;

    @NotBlank(message = "La calle es requerida")
    @Size(max = 100, message = "La calle no puede exceder 100 caracteres")
    private String calle;

    @NotBlank(message = "El número es requerido")
    @Size(max = 10, message = "El número no puede exceder 10 caracteres")
    private String numero;

    @NotBlank(message = "El código postal es requerido")
    @Pattern(regexp = "^[0-9]{5}$", message = "El código postal debe tener 5 dígitos")
    private String codigoPostal;
}
