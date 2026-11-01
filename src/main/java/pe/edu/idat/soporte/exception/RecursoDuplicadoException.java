package pe.edu.idat.soporte.exception;

/** Se lanza cuando se intenta registrar un dato único ya existente (HTTP 409). */
public class RecursoDuplicadoException extends RuntimeException {

    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
