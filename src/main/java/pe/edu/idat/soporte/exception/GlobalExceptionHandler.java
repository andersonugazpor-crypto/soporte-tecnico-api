package pe.edu.idat.soporte.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import pe.edu.idat.soporte.dto.response.ErrorResponse;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejo centralizado de excepciones.
 * <p>
 * Traduce cada excepción a un código HTTP adecuado y a un cuerpo
 * {@link ErrorResponse} con estructura uniforme, de modo que los
 * controladores no necesitan bloques try/catch.
 *
 * <table>
 *   <tr><th>Excepción</th><th>HTTP</th></tr>
 *   <tr><td>Validación (@Valid)</td><td>400</td></tr>
 *   <tr><td>JSON mal formado / enum inválido</td><td>400</td></tr>
 *   <tr><td>Parámetro de tipo incorrecto</td><td>400</td></tr>
 *   <tr><td>RecursoNoEncontradoException / ruta inexistente</td><td>404</td></tr>
 *   <tr><td>Método HTTP no soportado</td><td>405</td></tr>
 *   <tr><td>RecursoDuplicadoException</td><td>409</td></tr>
 *   <tr><td>Content-Type no soportado</td><td>415</td></tr>
 *   <tr><td>ReglaNegocioException</td><td>422</td></tr>
 *   <tr><td>Cualquier otra</td><td>500</td></tr>
 * </table>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---------- Errores de negocio ----------

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNoEncontrado(RecursoNoEncontradoException ex,
                                                            HttpServletRequest request) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleDuplicado(RecursoDuplicadoException ex,
                                                         HttpServletRequest request) {
        return construir(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> handleReglaNegocio(ReglaNegocioException ex,
                                                            HttpServletRequest request) {
        return construir(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
    }

    // ---------- Errores de validación y formato ----------

    /** Errores de @Valid sobre el cuerpo de la petición: devuelve campo → mensaje. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> errores.putIfAbsent(fe.getField(), fe.getDefaultMessage()));

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse body = ErrorResponse.of(status.value(), status.getReasonPhrase(),
                "Los datos enviados no son válidos", request.getRequestURI(), errores);
        return ResponseEntity.status(status).body(body);
    }

    /** Errores de validación sobre parámetros (@PathVariable, @RequestParam). */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidacionParametros(HandlerMethodValidationException ex,
                                                                    HttpServletRequest request) {
        String detalle = ex.getAllErrors().stream()
                .map(e -> e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return construir(HttpStatus.BAD_REQUEST, "Parámetros inválidos: " + detalle, request);
    }

    /** JSON mal formado o valor no válido para un enum (p. ej. "prioridad": "URGENTE"). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonInvalido(HttpMessageNotReadableException ex,
                                                            HttpServletRequest request) {
        String mensaje = "El cuerpo de la petición no es un JSON válido";
        if (ex.getCause() instanceof InvalidFormatException ife && ife.getTargetType() != null) {
            String campo = ife.getPath().isEmpty() ? "valor" : ife.getPath().get(0).getFieldName();
            if (ife.getTargetType().isEnum()) {
                String permitidos = Arrays.stream(ife.getTargetType().getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));
                mensaje = "Valor '" + ife.getValue() + "' no válido para '" + campo
                        + "'. Valores permitidos: " + permitidos;
            } else {
                mensaje = "Valor '" + ife.getValue() + "' no válido para '" + campo + "'";
            }
        }
        return construir(HttpStatus.BAD_REQUEST, mensaje, request);
    }

    /** Parámetro de ruta o query con tipo incorrecto, p. ej. /solicitudes/abc. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTipoIncorrecto(MethodArgumentTypeMismatchException ex,
                                                              HttpServletRequest request) {
        String mensaje = "El parámetro '" + ex.getName() + "' tiene un valor inválido: '" + ex.getValue() + "'";
        Class<?> tipo = ex.getRequiredType();
        if (tipo != null && tipo.isEnum()) {
            String permitidos = Arrays.stream(tipo.getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            mensaje += ". Valores permitidos: " + permitidos;
        }
        return construir(HttpStatus.BAD_REQUEST, mensaje, request);
    }

    // ---------- Errores de la petición HTTP ----------

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleRutaInexistente(NoResourceFoundException ex,
                                                               HttpServletRequest request) {
        return construir(HttpStatus.NOT_FOUND, "La ruta solicitada no existe", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMetodoNoSoportado(HttpRequestMethodNotSupportedException ex,
                                                                 HttpServletRequest request) {
        return construir(HttpStatus.METHOD_NOT_ALLOWED,
                "El método " + ex.getMethod() + " no está permitido en esta ruta", request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaType(HttpMediaTypeNotSupportedException ex,
                                                         HttpServletRequest request) {
        return construir(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "El Content-Type debe ser application/json", request);
    }

    // ---------- Cualquier otro error ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado en {}", request.getRequestURI(), ex);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno. Intente nuevamente más tarde", request);
    }

    private ResponseEntity<ErrorResponse> construir(HttpStatus status, String mensaje,
                                                    HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.of(status.value(), status.getReasonPhrase(),
                mensaje, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
