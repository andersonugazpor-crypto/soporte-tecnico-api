package pe.edu.idat.soporte.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pe.edu.idat.soporte.model.enums.Especialidad;

/**
 * Datos de entrada para registrar o actualizar un técnico.
 */
@Schema(description = "Datos para registrar o actualizar un técnico")
public record TecnicoRequest(

        @Schema(example = "Luis")
        @NotBlank(message = "Los nombres son obligatorios")
        @Size(min = 2, max = 60, message = "Los nombres deben tener entre 2 y 60 caracteres")
        String nombres,

        @Schema(example = "Huamán Torres")
        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(min = 2, max = 60, message = "Los apellidos deben tener entre 2 y 60 caracteres")
        String apellidos,

        @Schema(example = "luis.huaman@soporte.pe")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @Schema(example = "912345678")
        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^9\\d{8}$", message = "El teléfono debe tener 9 dígitos y empezar con 9")
        String telefono,

        @Schema(example = "REDES", description = "HARDWARE, SOFTWARE, REDES o SEGURIDAD")
        @NotNull(message = "La especialidad es obligatoria")
        Especialidad especialidad,

        @Schema(example = "true", description = "Opcional. Si no se envía, el técnico queda activo")
        Boolean activo
) {
}
