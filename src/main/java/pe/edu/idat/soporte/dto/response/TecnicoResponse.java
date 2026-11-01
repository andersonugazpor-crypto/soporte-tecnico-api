package pe.edu.idat.soporte.dto.response;

import pe.edu.idat.soporte.model.enums.Especialidad;

import java.time.LocalDateTime;

/** Representación de un técnico en las respuestas de la API. */
public record TecnicoResponse(
        Long id,
        String nombres,
        String apellidos,
        String nombreCompleto,
        String email,
        String telefono,
        Especialidad especialidad,
        boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion
) {
}
