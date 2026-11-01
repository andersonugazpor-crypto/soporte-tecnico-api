package pe.edu.idat.soporte.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Estructura estándar para todas las respuestas de error.
 * <pre>
 * {
 *   "exito": false,
 *   "status": 400,
 *   "error": "Bad Request",
 *   "mensaje": "Los datos enviados no son válidos",
 *   "ruta": "/api/v1/solicitudes",
 *   "errores": { "titulo": "El título es obligatorio" },
 *   "timestamp": "..."
 * }
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
        boolean exito,
        int status,
        String error,
        String mensaje,
        String ruta,
        Map<String, String> errores,
        LocalDateTime timestamp
) {

    public static ErrorResponse of(int status, String error, String mensaje, String ruta) {
        return new ErrorResponse(false, status, error, mensaje, ruta, null, LocalDateTime.now());
    }

    public static ErrorResponse of(int status, String error, String mensaje, String ruta,
                                   Map<String, String> errores) {
        return new ErrorResponse(false, status, error, mensaje, ruta, errores, LocalDateTime.now());
    }
}
