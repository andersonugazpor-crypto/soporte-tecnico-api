package pe.edu.idat.soporte.repository;

import pe.edu.idat.soporte.model.EntidadBase;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

/**
 * Implementación genérica de persistencia en memoria.
 * <p>
 * Simula el comportamiento de una tabla de base de datos:
 * <ul>
 *   <li>{@link ConcurrentHashMap} como "tabla", indexada por id (búsqueda O(1)).</li>
 *   <li>{@link AtomicLong} como secuencia autoincremental (ids únicos, nunca reutilizados).</li>
 *   <li>Auditoría automática de fechas de creación y actualización.</li>
 * </ul>
 * Es segura para peticiones concurrentes, ya que Spring atiende cada
 * petición HTTP en un hilo distinto.
 *
 * @param <T> tipo de entidad
 */
public abstract class InMemoryRepository<T extends EntidadBase> implements CrudRepository<T> {

    protected final Map<Long, T> almacen = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong(0);

    @Override
    public T save(T entidad) {
        LocalDateTime ahora = LocalDateTime.now();
        if (entidad.getId() == null) {
            entidad.setId(secuencia.incrementAndGet());
            entidad.setFechaCreacion(ahora);
        }
        entidad.setFechaActualizacion(ahora);
        almacen.put(entidad.getId(), entidad);
        return entidad;
    }

    @Override
    public Optional<T> findById(Long id) {
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public List<T> findAll() {
        return almacen.values().stream()
                .sorted(Comparator.comparing(EntidadBase::getId))
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return almacen.containsKey(id);
    }

    @Override
    public void deleteById(Long id) {
        almacen.remove(id);
    }

    @Override
    public long count() {
        return almacen.size();
    }

    /** Equivalente a un SELECT ... WHERE condición ORDER BY id. */
    protected List<T> findWhere(Predicate<T> condicion) {
        return almacen.values().stream()
                .filter(condicion)
                .sorted(Comparator.comparing(EntidadBase::getId))
                .toList();
    }
}
