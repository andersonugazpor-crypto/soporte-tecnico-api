package pe.edu.idat.soporte.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import pe.edu.idat.soporte.model.enums.Prioridad;

/**
 * Datos de entrada para registrar o actualizar una solicitud de soporte.
 * El estado no se envía aquí: se gestiona con su propio endpoint
 * para respetar las reglas de transición.
 */
@Schema(description = "Datos para registrar o actualizar una solicitud de soporte")
public record SolicitudRequest(

        @Schema(example = "La impresora no imprime")
        @NotBlank(message = "El título es obligatorio")
        @Size(min = 5, max = 100, message = "El título debe tener entre 5 y 100 caracteres")
        String titulo,

        @Schema(example = "La impresora del área de ventas muestra error de atasco aunque no tiene papel atascado.")
        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 10, max = 1000, message = "La descripción debe tener entre 10 y 1000 caracteres")
        String descripcion,

        @Schema(example = "ALTA", description = "BAJA, MEDIA, ALTA o CRITICA")
        @NotNull(message = "La prioridad es obligatoria")
        Prioridad prioridad,

        @Schema(example = "1")
        @NotNull(message = "El id del cliente es obligatorio")
        @Positive(message = "El id del cliente debe ser un número positivo")
        Long clienteId
) {
}
