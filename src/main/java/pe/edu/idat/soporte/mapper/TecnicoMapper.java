package pe.edu.idat.soporte.mapper;

import org.springframework.stereotype.Component;
import pe.edu.idat.soporte.dto.request.TecnicoRequest;
import pe.edu.idat.soporte.dto.response.TecnicoResponse;
import pe.edu.idat.soporte.model.Tecnico;

/** Convierte entre la entidad {@link Tecnico} y sus DTOs. */
@Component
public class TecnicoMapper {

    public Tecnico toEntity(TecnicoRequest request) {
        Tecnico tecnico = new Tecnico();
        updateEntity(tecnico, request);
        return tecnico;
    }

    public void updateEntity(Tecnico tecnico, TecnicoRequest request) {
        tecnico.setNombres(request.nombres().trim());
        tecnico.setApellidos(request.apellidos().trim());
        tecnico.setEmail(request.email().trim().toLowerCase());
        tecnico.setTelefono(request.telefono());
        tecnico.setEspecialidad(request.especialidad());
        tecnico.setActivo(request.activo() == null || request.activo());
    }

    public TecnicoResponse toResponse(Tecnico t) {
        return new TecnicoResponse(
                t.getId(), t.getNombres(), t.getApellidos(), t.getNombreCompleto(),
                t.getEmail(), t.getTelefono(), t.getEspecialidad(), t.isActivo(),
                t.getFechaCreacion(), t.getFechaActualizacion());
    }
}
