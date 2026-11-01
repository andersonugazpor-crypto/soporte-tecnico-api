package pe.edu.idat.soporte.model;

import pe.edu.idat.soporte.model.enums.EstadoSolicitud;
import pe.edu.idat.soporte.model.enums.Prioridad;

import java.time.LocalDateTime;

/**
 * Solicitud de soporte técnico registrada por un cliente.
 * <p>
 * Las relaciones con Cliente y Técnico se guardan por identificador,
 * igual que una clave foránea en una base de datos relacional.
 */
public class Solicitud extends EntidadBase {

    private String titulo;
    private String descripcion;
    private Prioridad prioridad;
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;
    private Long clienteId;
    private Long tecnicoId;
    private LocalDateTime fechaCierre;

    public Solicitud() {
    }

    public Solicitud(String titulo, String descripcion, Prioridad prioridad, Long clienteId) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.clienteId = clienteId;
        this.estado = EstadoSolicitud.PENDIENTE;
    }

    /** Una solicitud es editable mientras no esté finalizada. */
    public boolean esEditable() {
        return !estado.esFinal();
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(Long tecnicoId) {
        this.tecnicoId = tecnicoId;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }
}
