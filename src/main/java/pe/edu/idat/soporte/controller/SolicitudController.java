package pe.edu.idat.soporte.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.idat.soporte.dto.request.AsignacionTecnicoRequest;
import pe.edu.idat.soporte.dto.request.CambioEstadoRequest;
import pe.edu.idat.soporte.dto.request.SolicitudRequest;
import pe.edu.idat.soporte.dto.response.ApiResponse;
import pe.edu.idat.soporte.dto.response.SolicitudResponse;
import pe.edu.idat.soporte.model.enums.EstadoSolicitud;
import pe.edu.idat.soporte.model.enums.Prioridad;
import pe.edu.idat.soporte.service.SolicitudService;

import java.net.URI;
import java.util.List;

/**
 * Endpoints CRUD de solicitudes de soporte técnico, más las operaciones
 * de negocio de cambio de estado y asignación de técnico.
 */
@RestController
@RequestMapping("/api/v1/solicitudes")
@Tag(name = "Solicitudes", description = "Registro, consulta, actualización y eliminación de solicitudes de soporte")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @GetMapping
    @Operation(summary = "Listar solicitudes", description = "Todos los filtros son opcionales y combinables")
    public ResponseEntity<ApiResponse<List<SolicitudResponse>>> listar(
            @Parameter(description = "PENDIENTE, EN_PROCESO, RESUELTA, CERRADA o CANCELADA")
            @RequestParam(required = false) EstadoSolicitud estado,
            @Parameter(description = "BAJA, MEDIA, ALTA o CRITICA")
            @RequestParam(required = false) Prioridad prioridad,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long tecnicoId) {
        return ResponseEntity.ok(ApiResponse.lista("Listado de solicitudes",
                solicitudService.listar(estado, prioridad, clienteId, tecnicoId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una solicitud por id")
    public ResponseEntity<ApiResponse<SolicitudResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Solicitud encontrada", solicitudService.obtenerPorId(id)));
    }

    @PostMapping
    @Operation(summary = "Registrar una solicitud",
            description = "Se crea en estado PENDIENTE. 404 si el cliente no existe")
    public ResponseEntity<ApiResponse<SolicitudResponse>> registrar(@Valid @RequestBody SolicitudRequest request) {
        SolicitudResponse creada = solicitudService.registrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.id()).toUri();
        return ResponseEntity.created(location).body(ApiResponse.ok("Solicitud registrada correctamente", creada));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una solicitud",
            description = "Modifica título, descripción, prioridad y cliente. 422 si está CERRADA o CANCELADA")
    public ResponseEntity<ApiResponse<SolicitudResponse>> actualizar(@PathVariable Long id,
                                                                     @Valid @RequestBody SolicitudRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Solicitud actualizada correctamente",
                solicitudService.actualizar(id, request)));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado de una solicitud",
            description = "Valida las transiciones: PENDIENTE→EN_PROCESO|CANCELADA, EN_PROCESO→RESUELTA|CANCELADA, "
                    + "RESUELTA→CERRADA|EN_PROCESO. 422 si la transición no está permitida")
    public ResponseEntity<ApiResponse<SolicitudResponse>> cambiarEstado(@PathVariable Long id,
                                                                        @Valid @RequestBody CambioEstadoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado a " + request.estado(),
                solicitudService.cambiarEstado(id, request.estado())));
    }

    @PatchMapping("/{id}/tecnico")
    @Operation(summary = "Asignar un técnico a la solicitud",
            description = "Si la solicitud estaba PENDIENTE pasa a EN_PROCESO. 422 si el técnico está inactivo")
    public ResponseEntity<ApiResponse<SolicitudResponse>> asignarTecnico(@PathVariable Long id,
                                                                         @Valid @RequestBody AsignacionTecnicoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Técnico asignado correctamente",
                solicitudService.asignarTecnico(id, request.tecnicoId())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una solicitud")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        solicitudService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.mensaje("Solicitud " + id + " eliminada correctamente"));
    }
}
