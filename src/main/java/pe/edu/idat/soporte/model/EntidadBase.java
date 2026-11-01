package pe.edu.idat.soporte.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Clase base abstracta para todas las entidades del dominio.
 * <p>
 * Concentra los atributos comunes (identificador y fechas de auditoría)
 * para reutilizarlos mediante herencia y que el repositorio genérico en
 * memoria pueda trabajar con cualquier entidad.
 */
public abstract class EntidadBase {

    private Long id;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    protected EntidadBase() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntidadBase that = (EntidadBase) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), id);
    }
}
