package pe.edu.idat.soporte.service;

import pe.edu.idat.soporte.dto.request.ClienteRequest;
import pe.edu.idat.soporte.dto.response.ClienteResponse;

import java.util.List;

/** Operaciones de negocio sobre clientes. */
public interface ClienteService {

    List<ClienteResponse> listar();

    ClienteResponse obtenerPorId(Long id);

    ClienteResponse registrar(ClienteRequest request);

    ClienteResponse actualizar(Long id, ClienteRequest request);

    /** Elimina el cliente si no tiene solicitudes activas. */
    void eliminar(Long id);
}
