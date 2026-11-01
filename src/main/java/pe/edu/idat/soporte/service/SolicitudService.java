package pe.edu.idat.soporte.service;

import pe.edu.idat.soporte.dto.request.SolicitudRequest;
import pe.edu.idat.soporte.dto.response.SolicitudResponse;
import pe.edu.idat.soporte.model.enums.EstadoSolicitud;
import pe.edu.idat.soporte.model.enums.Prioridad;

import java.util.List;

/** Operaciones de negocio sobre solicitudes de soporte técnico. */
public interface SolicitudService {

    /** Lista solicitudes aplicando los filtros que no sean null. */
    List<SolicitudResponse> listar(EstadoSolicitud estado, Prioridad prioridad, Long clienteId, Long tecnicoId);

    SolicitudResponse obtenerPorId(Long id);

    SolicitudResponse registrar(SolicitudRequest request);

    SolicitudResponse actualizar(Long id, SolicitudRequest request);

    void eliminar(Long id);

    /** Cambia el estado validando que la transición esté permitida. */
    SolicitudResponse cambiarEstado(Long id, EstadoSolicitud nuevoEstado);

    /** Asigna un técnico activo; si la solicitud estaba PENDIENTE pasa a EN_PROCESO. */
    SolicitudResponse asignarTecnico(Long id, Long tecnicoId);

    List<SolicitudResponse> listarPorCliente(Long clienteId);

    List<SolicitudResponse> listarPorTecnico(Long tecnicoId);
}
