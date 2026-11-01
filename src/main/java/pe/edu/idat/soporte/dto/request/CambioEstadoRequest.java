package pe.edu.idat.soporte.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import pe.edu.idat.soporte.model.enums.EstadoSolicitud;

/** Cambio de estado de una solicitud. */
@Schema(description = "Nuevo estado de la solicitud")
public record CambioEstadoRequest(

        @Schema(example = "RESUELTA", description = "PENDIENTE, EN_PROCESO, RESUELTA, CERRADA o CANCELADA")
        @NotNull(message = "El estado es obligatorio")
        EstadoSolicitud estado
) {
}
