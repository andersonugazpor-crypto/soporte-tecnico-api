package pe.edu.idat.soporte.service.impl;

import org.springframework.stereotype.Service;
import pe.edu.idat.soporte.dto.request.SolicitudRequest;
import pe.edu.idat.soporte.dto.response.SolicitudResponse;
import pe.edu.idat.soporte.exception.RecursoNoEncontradoException;
import pe.edu.idat.soporte.exception.ReglaNegocioException;
import pe.edu.idat.soporte.mapper.SolicitudMapper;
import pe.edu.idat.soporte.model.Solicitud;
import pe.edu.idat.soporte.model.Tecnico;
import pe.edu.idat.soporte.model.enums.EstadoSolicitud;
import pe.edu.idat.soporte.model.enums.Prioridad;
import pe.edu.idat.soporte.repository.ClienteRepository;
import pe.edu.idat.soporte.repository.SolicitudRepository;
import pe.edu.idat.soporte.repository.TecnicoRepository;
import pe.edu.idat.soporte.service.SolicitudService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    private static final String RECURSO = "Solicitud";

    private final SolicitudRepository solicitudRepository;
    private final ClienteRepository clienteRepository;
    private final TecnicoRepository tecnicoRepository;
    private final SolicitudMapper mapper;

    public SolicitudServiceImpl(SolicitudRepository solicitudRepository,
                                ClienteRepository clienteRepository,
                                TecnicoRepository tecnicoRepository,
                                SolicitudMapper mapper) {
        this.solicitudRepository = solicitudRepository;
        this.clienteRepository = clienteRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.mapper = mapper;
    }

    @Override
    public List<SolicitudResponse> listar(EstadoSolicitud estado, Prioridad prioridad,
                                          Long clienteId, Long tecnicoId) {
        return solicitudRepository.buscar(estado, prioridad, clienteId, tecnicoId)
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    public SolicitudResponse obtenerPorId(Long id) {
        return mapper.toResponse(buscar(id));
    }

    @Override
    public SolicitudResponse registrar(SolicitudRequest request) {
        validarClienteExiste(request.clienteId());
        Solicitud nueva = mapper.toEntity(request);
        return mapper.toResponse(solicitudRepository.save(nueva));
    }

    @Override
    public SolicitudResponse actualizar(Long id, SolicitudRequest request) {
        Solicitud solicitud = buscar(id);
        validarEditable(solicitud);
        validarClienteExiste(request.clienteId());
        mapper.updateEntity(solicitud, request);
        return mapper.toResponse(solicitudRepository.save(solicitud));
    }

    @Override
    public void eliminar(Long id) {
        buscar(id);
        solicitudRepository.deleteById(id);
    }

    @Override
    public SolicitudResponse cambiarEstado(Long id, EstadoSolicitud nuevoEstado) {
        Solicitud solicitud = buscar(id);
        EstadoSolicitud actual = solicitud.getEstado();

        if (actual == nuevoEstado) {
            throw new ReglaNegocioException("La solicitud " + id + " ya se encuentra en estado " + actual);
        }
        if (!actual.puedeCambiarA(nuevoEstado)) {
            throw new ReglaNegocioException("No se puede pasar de " + actual + " a " + nuevoEstado
                    + ". Transiciones permitidas desde " + actual + ": " + actual.transicionesPermitidas());
        }
        if (nuevoEstado == EstadoSolicitud.EN_PROCESO && solicitud.getTecnicoId() == null) {
            throw new ReglaNegocioException(
                    "Asigne un técnico a la solicitud " + id + " antes de ponerla EN_PROCESO");
        }

        solicitud.setEstado(nuevoEstado);
        // fechaCierre se registra al resolver/cancelar y se limpia si se reabre
        if (nuevoEstado == EstadoSolicitud.RESUELTA || nuevoEstado == EstadoSolicitud.CANCELADA) {
            solicitud.setFechaCierre(LocalDateTime.now());
        } else if (nuevoEstado == EstadoSolicitud.EN_PROCESO) {
            solicitud.setFechaCierre(null);
        }
        return mapper.toResponse(solicitudRepository.save(solicitud));
    }

    @Override
    public SolicitudResponse asignarTecnico(Long id, Long tecnicoId) {
        Solicitud solicitud = buscar(id);
        validarEditable(solicitud);
        if (solicitud.getEstado() == EstadoSolicitud.RESUELTA) {
            throw new ReglaNegocioException("La solicitud " + id + " ya fue resuelta; reábrala para reasignarla");
        }

        Tecnico tecnico = tecnicoRepository.findById(tecnicoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Técnico", tecnicoId));
        if (!tecnico.isActivo()) {
            throw new ReglaNegocioException("El técnico " + tecnicoId + " está inactivo y no puede recibir solicitudes");
        }

        solicitud.setTecnicoId(tecnicoId);
        if (solicitud.getEstado() == EstadoSolicitud.PENDIENTE) {
            solicitud.setEstado(EstadoSolicitud.EN_PROCESO);
        }
        return mapper.toResponse(solicitudRepository.save(solicitud));
    }

    @Override
    public List<SolicitudResponse> listarPorCliente(Long clienteId) {
        validarClienteExiste(clienteId);
        return solicitudRepository.findByClienteId(clienteId).stream().map(mapper::toResponse).toList();
    }

    @Override
    public List<SolicitudResponse> listarPorTecnico(Long tecnicoId) {
        if (!tecnicoRepository.existsById(tecnicoId)) {
            throw new RecursoNoEncontradoException("Técnico", tecnicoId);
        }
        return solicitudRepository.findByTecnicoId(tecnicoId).stream().map(mapper::toResponse).toList();
    }

    // ---------- Métodos de apoyo ----------

    private Solicitud buscar(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(RECURSO, id));
    }

    private void validarClienteExiste(Long clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new RecursoNoEncontradoException("Cliente", clienteId);
        }
    }

    private void validarEditable(Solicitud solicitud) {
        if (!solicitud.esEditable()) {
            throw new ReglaNegocioException("La solicitud " + solicitud.getId()
                    + " está " + solicitud.getEstado() + " y ya no puede modificarse");
        }
    }
}
