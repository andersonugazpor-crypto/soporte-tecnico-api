package pe.edu.idat.soporte.service.impl;

import org.springframework.stereotype.Service;
import pe.edu.idat.soporte.dto.request.TecnicoRequest;
import pe.edu.idat.soporte.dto.response.TecnicoResponse;
import pe.edu.idat.soporte.exception.RecursoDuplicadoException;
import pe.edu.idat.soporte.exception.RecursoNoEncontradoException;
import pe.edu.idat.soporte.exception.ReglaNegocioException;
import pe.edu.idat.soporte.mapper.TecnicoMapper;
import pe.edu.idat.soporte.model.Tecnico;
import pe.edu.idat.soporte.model.enums.Especialidad;
import pe.edu.idat.soporte.repository.SolicitudRepository;
import pe.edu.idat.soporte.repository.TecnicoRepository;
import pe.edu.idat.soporte.service.TecnicoService;

import java.util.List;
import java.util.Objects;

@Service
public class TecnicoServiceImpl implements TecnicoService {

    private static final String RECURSO = "Técnico";

    private final TecnicoRepository tecnicoRepository;
    private final SolicitudRepository solicitudRepository;
    private final TecnicoMapper mapper;

    public TecnicoServiceImpl(TecnicoRepository tecnicoRepository,
                              SolicitudRepository solicitudRepository,
                              TecnicoMapper mapper) {
        this.tecnicoRepository = tecnicoRepository;
        this.solicitudRepository = solicitudRepository;
        this.mapper = mapper;
    }

    @Override
    public List<TecnicoResponse> listar(Especialidad especialidad) {
        List<Tecnico> tecnicos = especialidad == null
                ? tecnicoRepository.findAll()
                : tecnicoRepository.findByEspecialidad(especialidad);
        return tecnicos.stream().map(mapper::toResponse).toList();
    }

    @Override
    public TecnicoResponse obtenerPorId(Long id) {
        return mapper.toResponse(buscar(id));
    }

    @Override
    public TecnicoResponse registrar(TecnicoRequest request) {
        validarEmailUnico(request.email(), null);
        return mapper.toResponse(tecnicoRepository.save(mapper.toEntity(request)));
    }

    @Override
    public TecnicoResponse actualizar(Long id, TecnicoRequest request) {
        Tecnico tecnico = buscar(id);
        validarEmailUnico(request.email(), id);
        boolean seDesactiva = tecnico.isActivo() && Boolean.FALSE.equals(request.activo());
        if (seDesactiva && solicitudRepository.existenActivasDeTecnico(id)) {
            throw new ReglaNegocioException(
                    "No se puede desactivar al técnico " + id + " porque tiene solicitudes en curso");
        }
        mapper.updateEntity(tecnico, request);
        return mapper.toResponse(tecnicoRepository.save(tecnico));
    }

    @Override
    public void eliminar(Long id) {
        buscar(id);
        if (solicitudRepository.existenActivasDeTecnico(id)) {
            throw new ReglaNegocioException(
                    "No se puede eliminar el técnico " + id + " porque tiene solicitudes en curso asignadas");
        }
        // Las solicitudes finalizadas se conservan, pero quedan sin técnico asignado
        solicitudRepository.desasignarTecnico(id);
        tecnicoRepository.deleteById(id);
    }

    private Tecnico buscar(Long id) {
        return tecnicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(RECURSO, id));
    }

    private void validarEmailUnico(String email, Long idActual) {
        tecnicoRepository.findByEmail(email.trim())
                .filter(t -> !Objects.equals(t.getId(), idActual))
                .ifPresent(t -> {
                    throw new RecursoDuplicadoException("Ya existe un técnico con el email " + email);
                });
    }
}
