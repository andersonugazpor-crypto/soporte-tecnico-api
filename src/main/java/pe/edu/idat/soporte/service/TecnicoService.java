package pe.edu.idat.soporte.service;

import pe.edu.idat.soporte.dto.request.TecnicoRequest;
import pe.edu.idat.soporte.dto.response.TecnicoResponse;
import pe.edu.idat.soporte.model.enums.Especialidad;

import java.util.List;

/** Operaciones de negocio sobre técnicos. */
public interface TecnicoService {

    /** Lista todos los técnicos o solo los de una especialidad (si no es null). */
    List<TecnicoResponse> listar(Especialidad especialidad);

    TecnicoResponse obtenerPorId(Long id);

    TecnicoResponse registrar(TecnicoRequest request);

    TecnicoResponse actualizar(Long id, TecnicoRequest request);

    /** Elimina el técnico si no tiene solicitudes activas asignadas. */
    void eliminar(Long id);
}
