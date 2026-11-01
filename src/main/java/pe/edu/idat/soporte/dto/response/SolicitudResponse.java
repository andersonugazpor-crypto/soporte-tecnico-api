package pe.edu.idat.soporte.dto.response;

import pe.edu.idat.soporte.model.enums.EstadoSolicitud;
import pe.edu.idat.soporte.model.enums.Prioridad;

import java.time.LocalDateTime;

/**
 * Representación de una solicitud en las respuestas de la API.
 * Incluye un resumen del cliente y del técnico para que el consumidor
 * no tenga que hacer peticiones adicionales.
 */
public record SolicitudResponse(
        Long id,
        String titulo,
        String descripcion,
        Prioridad prioridad,
        EstadoSolicitud estado,
        PersonaResumen cliente,
        PersonaResumen tecnico,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        LocalDateTime fechaCierre
) {

    /** Datos mínimos de una persona relacionada. */
    public record PersonaResumen(Long id, String nombreCompleto, String email) {
    }
}
