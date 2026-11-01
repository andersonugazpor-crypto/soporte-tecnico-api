package pe.edu.idat.soporte.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos de entrada para registrar o actualizar un cliente.
 */
@Schema(description = "Datos para registrar o actualizar un cliente")
public record ClienteRequest(

        @Schema(example = "María")
        @NotBlank(message = "Los nombres son obligatorios")
        @Size(min = 2, max = 60, message = "Los nombres deben tener entre 2 y 60 caracteres")
        String nombres,

        @Schema(example = "Quispe Rojas")
        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(min = 2, max = 60, message = "Los apellidos deben tener entre 2 y 60 caracteres")
        String apellidos,

        @Schema(example = "maria.quispe@empresa.pe")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @Schema(example = "987654321")
        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^9\\d{8}$", message = "El teléfono debe tener 9 dígitos y empezar con 9")
        String telefono,

        @Schema(example = "45781236", description = "DNI (8 dígitos) o RUC (11 dígitos)")
        @NotBlank(message = "El documento es obligatorio")
        @Pattern(regexp = "^(\\d{8}|\\d{11})$", message = "El documento debe ser un DNI (8 dígitos) o RUC (11 dígitos)")
        String documento,

        @Schema(example = "Comercial Andina SAC")
        @Size(max = 100, message = "La empresa no puede superar los 100 caracteres")
        String empresa
) {
}
