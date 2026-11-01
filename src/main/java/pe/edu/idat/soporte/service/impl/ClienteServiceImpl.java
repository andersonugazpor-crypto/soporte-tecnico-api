package pe.edu.idat.soporte.service.impl;

import org.springframework.stereotype.Service;
import pe.edu.idat.soporte.dto.request.ClienteRequest;
import pe.edu.idat.soporte.dto.response.ClienteResponse;
import pe.edu.idat.soporte.exception.RecursoDuplicadoException;
import pe.edu.idat.soporte.exception.RecursoNoEncontradoException;
import pe.edu.idat.soporte.exception.ReglaNegocioException;
import pe.edu.idat.soporte.mapper.ClienteMapper;
import pe.edu.idat.soporte.model.Cliente;
import pe.edu.idat.soporte.repository.ClienteRepository;
import pe.edu.idat.soporte.repository.SolicitudRepository;
import pe.edu.idat.soporte.service.ClienteService;

import java.util.List;
import java.util.Objects;

@Service
public class ClienteServiceImpl implements ClienteService {

    private static final String RECURSO = "Cliente";

    private final ClienteRepository clienteRepository;
    private final SolicitudRepository solicitudRepository;
    private final ClienteMapper mapper;

    public ClienteServiceImpl(ClienteRepository clienteRepository,
                              SolicitudRepository solicitudRepository,
                              ClienteMapper mapper) {
        this.clienteRepository = clienteRepository;
        this.solicitudRepository = solicitudRepository;
        this.mapper = mapper;
    }

    @Override
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    public ClienteResponse obtenerPorId(Long id) {
        return mapper.toResponse(buscar(id));
    }

    @Override
    public ClienteResponse registrar(ClienteRequest request) {
        validarUnicidad(request, null);
        Cliente nuevo = mapper.toEntity(request);
        return mapper.toResponse(clienteRepository.save(nuevo));
    }

    @Override
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = buscar(id);
        validarUnicidad(request, id);
        mapper.updateEntity(cliente, request);
        return mapper.toResponse(clienteRepository.save(cliente));
    }

    @Override
    public void eliminar(Long id) {
        buscar(id);
        if (solicitudRepository.existenActivasDeCliente(id)) {
            throw new ReglaNegocioException(
                    "No se puede eliminar el cliente " + id + " porque tiene solicitudes pendientes o en proceso");
        }
        // El historial (solicitudes resueltas/cerradas/canceladas) se elimina junto con el cliente
        solicitudRepository.deleteByClienteId(id);
        clienteRepository.deleteById(id);
    }

    private Cliente buscar(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(RECURSO, id));
    }

    /** Email y documento deben ser únicos (excepto para el propio cliente que se actualiza). */
    private void validarUnicidad(ClienteRequest request, Long idActual) {
        clienteRepository.findByEmail(request.email().trim())
                .filter(c -> !Objects.equals(c.getId(), idActual))
                .ifPresent(c -> {
                    throw new RecursoDuplicadoException("Ya existe un cliente con el email " + request.email());
                });
        clienteRepository.findByDocumento(request.documento())
                .filter(c -> !Objects.equals(c.getId(), idActual))
                .ifPresent(c -> {
                    throw new RecursoDuplicadoException("Ya existe un cliente con el documento " + request.documento());
                });
    }
}
