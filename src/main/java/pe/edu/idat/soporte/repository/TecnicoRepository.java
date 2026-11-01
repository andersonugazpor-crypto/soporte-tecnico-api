package pe.edu.idat.soporte.repository;

import org.springframework.stereotype.Repository;
import pe.edu.idat.soporte.model.Tecnico;
import pe.edu.idat.soporte.model.enums.Especialidad;

import java.util.List;
import java.util.Optional;

/** Almacén en memoria de técnicos. */
@Repository
public class TecnicoRepository extends InMemoryRepository<Tecnico> {

    public Optional<Tecnico> findByEmail(String email) {
        return almacen.values().stream()
                .filter(t -> t.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public List<Tecnico> findByEspecialidad(Especialidad especialidad) {
        return findWhere(t -> t.getEspecialidad() == especialidad);
    }
}
