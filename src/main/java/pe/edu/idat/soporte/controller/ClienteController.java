package pe.edu.idat.soporte.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.idat.soporte.dto.request.ClienteRequest;
import pe.edu.idat.soporte.dto.response.ApiResponse;
import pe.edu.idat.soporte.dto.response.ClienteResponse;
import pe.edu.idat.soporte.dto.response.SolicitudResponse;
import pe.edu.idat.soporte.service.ClienteService;
import pe.edu.idat.soporte.service.SolicitudService;

import java.net.URI;
import java.util.List;

/**
 * Endpoints para la gestión de clientes.
 * Solo recibe la petición, delega al servicio y arma la respuesta HTTP.
 */
@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = "Clientes", description = "Registro y consulta de clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final SolicitudService solicitudService;

    public ClienteController(ClienteService clienteService, SolicitudService solicitudService) {
        this.clienteService = clienteService;
        this.solicitudService = solicitudService;
    }

    @GetMapping
    @Operation(summary = "Listar clientes")
    public ResponseEntity<ApiResponse<List<ClienteResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.lista("Listado de clientes", clienteService.listar()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un cliente por id", description = "404 si el cliente no existe")
    public ResponseEntity<ApiResponse<ClienteResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Cliente encontrado", clienteService.obtenerPorId(id)));
    }

    @GetMapping("/{id}/solicitudes")
    @Operation(summary = "Listar las solicitudes de un cliente")
    public ResponseEntity<ApiResponse<List<SolicitudResponse>>> solicitudes(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.lista("Solicitudes del cliente " + id,
                solicitudService.listarPorCliente(id)));
    }

    @PostMapping
    @Operation(summary = "Registrar un cliente",
            description = "201 si se crea; 400 si hay datos inválidos; 409 si el email o documento ya existen")
    public ResponseEntity<ApiResponse<ClienteResponse>> registrar(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse creado = clienteService.registrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(ApiResponse.ok("Cliente registrado correctamente", creado));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un cliente")
    public ResponseEntity<ApiResponse<ClienteResponse>> actualizar(@PathVariable Long id,
                                                                   @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Cliente actualizado correctamente",
                clienteService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un cliente",
            description = "422 si el cliente tiene solicitudes pendientes o en proceso")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.mensaje("Cliente " + id + " eliminado correctamente"));
    }
}
