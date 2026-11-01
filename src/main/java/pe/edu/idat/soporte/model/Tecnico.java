package pe.edu.idat.soporte.model;

import pe.edu.idat.soporte.model.enums.Especialidad;

/**
 * Técnico que atiende las solicitudes de soporte.
 */
public class Tecnico extends Persona {

    private Especialidad especialidad;
    private boolean activo = true;

    public Tecnico() {
    }

    public Tecnico(String nombres, String apellidos, String email, String telefono,
                   Especialidad especialidad) {
        super(nombres, apellidos, email, telefono);
        this.especialidad = especialidad;
        this.activo = true;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
