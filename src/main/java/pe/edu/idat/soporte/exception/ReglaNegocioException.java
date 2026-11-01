package pe.edu.idat.soporte.exception;

/**
 * Se lanza cuando la petición es válida en forma pero viola una regla
 * del negocio, p. ej. una transición de estado no permitida (HTTP 422).
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
