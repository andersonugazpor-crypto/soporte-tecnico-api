package pe.edu.idat.soporte.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pe.edu.idat.soporte.dto.request.SolicitudRequest;
import pe.edu.idat.soporte.dto.response.SolicitudResponse;
import pe.edu.idat.soporte.exception.RecursoNoEncontradoException;
import pe.edu.idat.soporte.exception.ReglaNegocioException;
import pe.edu.idat.soporte.mapper.SolicitudMapper;
import pe.edu.idat.soporte.model.Cliente;
import pe.edu.idat.soporte.model.Tecnico;
import pe.edu.idat.soporte.model.enums.Especialidad;
import pe.edu.idat.soporte.model.enums.EstadoSolicitud;
import pe.edu.idat.soporte.model.enums.Prioridad;
import pe.edu.idat.soporte.repository.ClienteRepository;
import pe.edu.idat.soporte.repository.SolicitudRepository;
import pe.edu.idat.soporte.repository.TecnicoRepository;
import pe.edu.idat.soporte.service.impl.SolicitudServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pruebas unitarias de la lógica de negocio de solicitudes.
 * Usa los repositorios en memoria reales, sin levantar Spring.
 */
class SolicitudServiceImplTest {

    private SolicitudService service;
    private Long clienteId;
    private Long tecnicoId;

    @BeforeEach
    void setUp() {
        ClienteRepository clienteRepo = new ClienteRepository();
        TecnicoRepository tecnicoRepo = new TecnicoRepository();
        SolicitudRepository solicitudRepo = new SolicitudRepository();
        service = new SolicitudServiceImpl(solicitudRepo, clienteRepo, tecnicoRepo,
                new SolicitudMapper(clienteRepo, tecnicoRepo));

        clienteId = clienteRepo.save(new Cliente("Ana", "Torres", "ana@test.pe", "987654321",
                "12345678", null)).getId();
        tecnicoId = tecnicoRepo.save(new Tecnico("Luis", "Huamán", "luis@test.pe", "912345678",
                Especialidad.REDES)).getId();
    }

    private SolicitudResponse crearSolicitud() {
        return service.registrar(new SolicitudRequest("Sin internet", "No hay conexión en la oficina",
                Prioridad.ALTA, clienteId));
    }

    @Test
    @DisplayName("Una solicitud nueva se registra en estado PENDIENTE con id autogenerado")
    void registrarSolicitud() {
        SolicitudResponse r = crearSolicitud();

        assertThat(r.id()).isEqualTo(1L);
        assertThat(r.estado()).isEqualTo(EstadoSolicitud.PENDIENTE);
        assertThat(r.cliente().nombreCompleto()).isEqualTo("Ana Torres");
        assertThat(r.fechaCreacion()).isNotNull();
    }

    @Test
    @DisplayName("No se puede registrar una solicitud para un cliente inexistente")
    void registrarConClienteInexistente() {
        SolicitudRequest req = new SolicitudRequest("Sin internet", "No hay conexión en la oficina",
                Prioridad.ALTA, 99L);

        assertThatThrownBy(() -> service.registrar(req))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Cliente con id 99");
    }

    @Test
    @DisplayName("Asignar técnico a una solicitud PENDIENTE la pasa a EN_PROCESO")
    void asignarTecnico() {
        Long id = crearSolicitud().id();

        SolicitudResponse r = service.asignarTecnico(id, tecnicoId);

        assertThat(r.estado()).isEqualTo(EstadoSolicitud.EN_PROCESO);
        assertThat(r.tecnico().id()).isEqualTo(tecnicoId);
    }

    @Test
    @DisplayName("No se puede pasar a EN_PROCESO sin técnico asignado")
    void enProcesoSinTecnico() {
        Long id = crearSolicitud().id();

        assertThatThrownBy(() -> service.cambiarEstado(id, EstadoSolicitud.EN_PROCESO))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    @DisplayName("Una transición no permitida (PENDIENTE → CERRADA) es rechazada")
    void transicionInvalida() {
        Long id = crearSolicitud().id();

        assertThatThrownBy(() -> service.cambiarEstado(id, EstadoSolicitud.CERRADA))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("No se puede pasar de PENDIENTE a CERRADA");
    }

    @Test
    @DisplayName("Flujo completo: asignar → resolver → cerrar; luego ya no es editable")
    void flujoCompleto() {
        Long id = crearSolicitud().id();
        service.asignarTecnico(id, tecnicoId);

        SolicitudResponse resuelta = service.cambiarEstado(id, EstadoSolicitud.RESUELTA);
        assertThat(resuelta.fechaCierre()).isNotNull();

        service.cambiarEstado(id, EstadoSolicitud.CERRADA);

        SolicitudRequest cambio = new SolicitudRequest("Nuevo título", "Nueva descripción larga",
                Prioridad.BAJA, clienteId);
        assertThatThrownBy(() -> service.actualizar(id, cambio))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ya no puede modificarse");
    }

    @Test
    @DisplayName("Los filtros de búsqueda se combinan")
    void filtros() {
        crearSolicitud();
        Long id2 = crearSolicitud().id();
        service.asignarTecnico(id2, tecnicoId);

        assertThat(service.listar(EstadoSolicitud.PENDIENTE, null, null, null)).hasSize(1);
        assertThat(service.listar(null, Prioridad.ALTA, clienteId, null)).hasSize(2);
        assertThat(service.listar(null, null, null, tecnicoId)).hasSize(1);
    }

    @Test
    @DisplayName("Eliminar una solicitud y luego consultarla devuelve 'no encontrado'")
    void eliminar() {
        Long id = crearSolicitud().id();
        service.eliminar(id);

        assertThatThrownBy(() -> service.obtenerPorId(id))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
