package pe.edu.idat.soporte.mapper;

import org.springframework.stereotype.Component;
import pe.edu.idat.soporte.dto.request.ClienteRequest;
import pe.edu.idat.soporte.dto.response.ClienteResponse;
import pe.edu.idat.soporte.model.Cliente;

/** Convierte entre la entidad {@link Cliente} y sus DTOs. */
@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteRequest request) {
        Cliente cliente = new Cliente();
        updateEntity(cliente, request);
        return cliente;
    }

    public void updateEntity(Cliente cliente, ClienteRequest request) {
        cliente.setNombres(request.nombres().trim());
        cliente.setApellidos(request.apellidos().trim());
        cliente.setEmail(request.email().trim().toLowerCase());
        cliente.setTelefono(request.telefono());
        cliente.setDocumento(request.documento());
        cliente.setEmpresa(request.empresa() == null ? null : request.empresa().trim());
    }

    public ClienteResponse toResponse(Cliente c) {
        return new ClienteResponse(
                c.getId(), c.getNombres(), c.getApellidos(), c.getNombreCompleto(),
                c.getEmail(), c.getTelefono(), c.getDocumento(), c.getEmpresa(),
                c.getFechaCreacion(), c.getFechaActualizacion());
    }
}
