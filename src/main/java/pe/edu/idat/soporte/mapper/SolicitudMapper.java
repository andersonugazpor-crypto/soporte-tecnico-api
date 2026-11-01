package pe.edu.idat.soporte.mapper;

import org.springframework.stereotype.Component;
import pe.edu.idat.soporte.dto.request.SolicitudRequest;
import pe.edu.idat.soporte.dto.response.SolicitudResponse;
import pe.edu.idat.soporte.dto.response.SolicitudResponse.PersonaResumen;
import pe.edu.idat.soporte.model.Persona;
import pe.edu.idat.soporte.model.Solicitud;
import pe.edu.idat.soporte.repository.ClienteRepository;
import pe.edu.idat.soporte.repository.TecnicoRepository;

/**
 * Convierte entre la entidad {@link Solicitud} y sus DTOs.
 * Resuelve los ids de cliente y técnico a un resumen legible.
 */
@Component
public class SolicitudMapper {

    private final ClienteRepository clienteRepository;
    private final TecnicoRepository tecnicoRepository;

    public SolicitudMapper(ClienteRepository clienteRepository, TecnicoRepository tecnicoRepository) {
        this.clienteRepository = clienteRepository;
        this.tecnicoRepository = tecnicoRepository;
    }

    public Solicitud toEntity(SolicitudRequest request) {
        return new Solicitud(
                request.titulo().trim(),
                request.descripcion().trim(),
                request.prioridad(),
                request.clienteId());
    }

    public void updateEntity(Solicitud solicitud, SolicitudRequest request) {
        solicitud.setTitulo(request.titulo().trim());
        solicitud.setDescripcion(request.descripcion().trim());
        solicitud.setPrioridad(request.prioridad());
        solicitud.setClienteId(request.clienteId());
    }

    public SolicitudResponse toResponse(Solicitud s) {
        PersonaResumen cliente = s.getClienteId() == null ? null
                : clienteRepository.findById(s.getClienteId()).map(this::resumen).orElse(null);
        PersonaResumen tecnico = s.getTecnicoId() == null ? null
                : tecnicoRepository.findById(s.getTecnicoId()).map(this::resumen).orElse(null);

        return new SolicitudResponse(
                s.getId(), s.getTitulo(), s.getDescripcion(), s.getPrioridad(), s.getEstado(),
                cliente, tecnico,
                s.getFechaCreacion(), s.getFechaActualizacion(), s.getFechaCierre());
    }

    private PersonaResumen resumen(Persona p) {
        return new PersonaResumen(p.getId(), p.getNombreCompleto(), p.getEmail());
    }
}
