package pe.edu.idat.soporte.repository;

import org.springframework.stereotype.Repository;
import pe.edu.idat.soporte.model.Cliente;

import java.util.Optional;

/** Almacén en memoria de clientes. */
@Repository
public class ClienteRepository extends InMemoryRepository<Cliente> {

    public Optional<Cliente> findByEmail(String email) {
        return almacen.values().stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Optional<Cliente> findByDocumento(String documento) {
        return almacen.values().stream()
                .filter(c -> c.getDocumento().equals(documento))
                .findFirst();
    }
}
