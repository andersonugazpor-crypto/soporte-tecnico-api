package pe.edu.idat.soporte.repository;

import pe.edu.idat.soporte.model.EntidadBase;

import java.util.List;
import java.util.Optional;

/**
 * Contrato genérico de acceso a datos.
 * <p>
 * Los servicios dependen de esta abstracción y no de la implementación
 * en memoria; si mañana se usa una base de datos real, solo cambia la
 * implementación.
 *
 * @param <T> tipo de entidad que gestiona el repositorio
 */
public interface CrudRepository<T extends EntidadBase> {

    /** Inserta (si no tiene id) o actualiza (si ya tiene id) la entidad. */
    T save(T entidad);

    Optional<T> findById(Long id);

    List<T> findAll();

    boolean existsById(Long id);

    void deleteById(Long id);

    long count();
}
