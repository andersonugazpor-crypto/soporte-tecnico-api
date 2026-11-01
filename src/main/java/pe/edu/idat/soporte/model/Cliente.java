package pe.edu.idat.soporte.model;

/**
 * Cliente que registra solicitudes de soporte técnico.
 */
public class Cliente extends Persona {

    /** DNI (8 dígitos) o RUC (11 dígitos). */
    private String documento;
    private String empresa;

    public Cliente() {
    }

    public Cliente(String nombres, String apellidos, String email, String telefono,
                   String documento, String empresa) {
        super(nombres, apellidos, email, telefono);
        this.documento = documento;
        this.empresa = empresa;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }
}
