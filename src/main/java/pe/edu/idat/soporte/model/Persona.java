package pe.edu.idat.soporte.model;

/**
 * Abstracción de una persona del sistema (cliente o técnico).
 * Agrupa los datos de contacto compartidos para evitar duplicación.
 */
public abstract class Persona extends EntidadBase {

    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;

    protected Persona() {
    }

    protected Persona(String nombres, String apellidos, String email, String telefono) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.telefono = telefono;
    }

    /** Devuelve "Nombres Apellidos". */
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
