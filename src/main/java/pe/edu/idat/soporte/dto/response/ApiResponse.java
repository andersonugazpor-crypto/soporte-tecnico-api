package pe.edu.idat.soporte.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Envoltura estándar para todas las respuestas exitosas.
 * <pre>
 * {
 *   "exito": true,
 *   "mensaje": "Solicitud registrada correctamente",
 *   "datos": { ... },
 *   "total": 3,            // solo en listados
 *   "timestamp": "..."
 * }
 * </pre>
 *
 * @param <T> tipo del contenido devuelto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean exito,
        String mensaje,
        T datos,
        Integer total,
        LocalDateTime timestamp
) {

    public static <T> ApiResponse<T> ok(String mensaje, T datos) {
        return new ApiResponse<>(true, mensaje, datos, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<java.util.List<T>> lista(String mensaje, java.util.List<T> datos) {
        return new ApiResponse<>(true, mensaje, datos, datos.size(), LocalDateTime.now());
    }

    public static ApiResponse<Void> mensaje(String mensaje) {
        return new ApiResponse<>(true, mensaje, null, null, LocalDateTime.now());
    }
}
