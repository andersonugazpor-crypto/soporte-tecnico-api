package pe.edu.idat.soporte.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.edu.idat.soporte.dto.request.TecnicoRequest;
import pe.edu.idat.soporte.dto.response.ApiResponse;
import pe.edu.idat.soporte.dto.response.SolicitudResponse;
import pe.edu.idat.soporte.dto.response.TecnicoResponse;
import pe.edu.idat.soporte.model.enums.Especialidad;
import pe.edu.idat.soporte.service.SolicitudService;
import pe.edu.idat.soporte.service.TecnicoService;

import java.net.URI;
import java.util.List;

/** Endpoints para la gestión de técnicos de soporte. */
@RestController
@RequestMapping("/api/v1/tecnicos")
@Tag(name = "Técnicos", description = "Registro y consulta de técnicos de soporte")
public class TecnicoController {

    private final TecnicoService tecnicoService;
    private final SolicitudService solicitudService;

    public TecnicoController(TecnicoService tecnicoService, SolicitudService solicitudService) {
        this.tecnicoService = tecnicoService;
        this.solicitudService = solicitudService;
    }

    @GetMapping
    @Operation(summary = "Listar técnicos", description = "Filtro opcional por especialidad")
    public ResponseEntity<ApiResponse<List<TecnicoResponse>>> listar(
            @Parameter(description = "HARDWARE, SOFTWARE, REDES o SEGURIDAD")
            @RequestParam(required = false) Especialidad especialidad) {
        return ResponseEntity.ok(ApiResponse.lista("Listado de técnicos", tecnicoService.listar(especialidad)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un técnico por id")
    public ResponseEntity<ApiResponse<TecnicoResponse>> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Técnico encontrado", tecnicoService.obtenerPorId(id)));
    }

    @GetMapping("/{id}/solicitudes")
    @Operation(summary = "Listar las solicitudes asignadas a un técnico")
    public ResponseEntity<ApiResponse<List<SolicitudResponse>>> solicitudes(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.lista("Solicitudes asignadas al técnico " + id,
                solicitudService.listarPorTecnico(id)));
    }

    @PostMapping
    @Operation(summary = "Registrar un técnico")
    public ResponseEntity<ApiResponse<TecnicoResponse>> registrar(@Valid @RequestBody TecnicoRequest request) {
        TecnicoResponse creado = tecnicoService.registrar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.id()).toUri();
        return ResponseEntity.created(location).body(ApiResponse.ok("Técnico registrado correctamente", creado));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un técnico")
    public ResponseEntity<ApiResponse<TecnicoResponse>> actualizar(@PathVariable Long id,
                                                                   @Valid @RequestBody TecnicoRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Técnico actualizado correctamente",
                tecnicoService.actualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un técnico",
            description = "422 si el técnico tiene solicitudes en curso asignadas")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        tecnicoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.mensaje("Técnico " + id + " eliminado correctamente"));
    }
}
