package pe.edu.idat.soporte.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Asignación de un técnico a una solicitud. */
@Schema(description = "Técnico que atenderá la solicitud")
public record AsignacionTecnicoRequest(

        @Schema(example = "1")
        @NotNull(message = "El id del técnico es obligatorio")
        @Positive(message = "El id del técnico debe ser un número positivo")
        Long tecnicoId
) {
}
