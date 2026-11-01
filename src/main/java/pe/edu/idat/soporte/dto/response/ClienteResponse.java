package pe.edu.idat.soporte.dto.response;

import java.time.LocalDateTime;

/** Representación de un cliente en las respuestas de la API. */
public record ClienteResponse(
        Long id,
        String nombres,
        String apellidos,
        String nombreCompleto,
        String email,
        String telefono,
        String documento,
        String empresa,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion
) {
}
