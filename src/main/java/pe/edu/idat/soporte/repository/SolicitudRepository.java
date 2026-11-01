package pe.edu.idat.soporte.repository;

import org.springframework.stereotype.Repository;
import pe.edu.idat.soporte.model.Solicitud;
import pe.edu.idat.soporte.model.enums.EstadoSolicitud;
import pe.edu.idat.soporte.model.enums.Prioridad;

import java.util.List;
import java.util.Objects;

/** Almacén en memoria de solicitudes de soporte. */
@Repository
public class SolicitudRepository extends InMemoryRepository<Solicitud> {

    /**
     * Búsqueda con filtros opcionales: un parámetro {@code null} no filtra.
     */
    public List<Solicitud> buscar(EstadoSolicitud estado, Prioridad prioridad,
                                  Long clienteId, Long tecnicoId) {
        return findWhere(s ->
                (estado == null || s.getEstado() == estado)
                        && (prioridad == null || s.getPrioridad() == prioridad)
                        && (clienteId == null || Objects.equals(s.getClienteId(), clienteId))
                        && (tecnicoId == null || Objects.equals(s.getTecnicoId(), tecnicoId)));
    }

    public List<Solicitud> findByClienteId(Long clienteId) {
        return findWhere(s -> Objects.equals(s.getClienteId(), clienteId));
    }

    public List<Solicitud> findByTecnicoId(Long tecnicoId) {
        return findWhere(s -> Objects.equals(s.getTecnicoId(), tecnicoId));
    }

    /** Equivalente a ON DELETE CASCADE: borra todas las solicitudes del cliente. */
    public void deleteByClienteId(Long clienteId) {
        almacen.values().removeIf(s -> Objects.equals(s.getClienteId(), clienteId));
    }

    /** Equivalente a ON DELETE SET NULL: desvincula al técnico de sus solicitudes. */
    public void desasignarTecnico(Long tecnicoId) {
        almacen.values().stream()
                .filter(s -> Objects.equals(s.getTecnicoId(), tecnicoId))
                .forEach(s -> s.setTecnicoId(null));
    }

    /** ¿El cliente tiene solicitudes pendientes o en proceso? */
    public boolean existenActivasDeCliente(Long clienteId) {
        return almacen.values().stream()
                .anyMatch(s -> Objects.equals(s.getClienteId(), clienteId) && s.getEstado().esActivo());
    }

    /** ¿El técnico tiene solicitudes pendientes o en proceso asignadas? */
    public boolean existenActivasDeTecnico(Long tecnicoId) {
        return almacen.values().stream()
                .anyMatch(s -> Objects.equals(s.getTecnicoId(), tecnicoId) && s.getEstado().esActivo());
    }
}
