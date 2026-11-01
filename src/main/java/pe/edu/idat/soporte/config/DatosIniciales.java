package pe.edu.idat.soporte.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pe.edu.idat.soporte.model.Cliente;
import pe.edu.idat.soporte.model.Solicitud;
import pe.edu.idat.soporte.model.Tecnico;
import pe.edu.idat.soporte.model.enums.Especialidad;
import pe.edu.idat.soporte.model.enums.EstadoSolicitud;
import pe.edu.idat.soporte.model.enums.Prioridad;
import pe.edu.idat.soporte.repository.ClienteRepository;
import pe.edu.idat.soporte.repository.SolicitudRepository;
import pe.edu.idat.soporte.repository.TecnicoRepository;

import java.time.LocalDateTime;

/**
 * Carga datos de ejemplo al iniciar, para poder probar la API de inmediato.
 * Se desactiva con {@code app.datos-iniciales=false}.
 */
@Component
@ConditionalOnProperty(name = "app.datos-iniciales", havingValue = "true", matchIfMissing = true)
public class DatosIniciales implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosIniciales.class);

    private final ClienteRepository clienteRepository;
    private final TecnicoRepository tecnicoRepository;
    private final SolicitudRepository solicitudRepository;

    public DatosIniciales(ClienteRepository clienteRepository,
                          TecnicoRepository tecnicoRepository,
                          SolicitudRepository solicitudRepository) {
        this.clienteRepository = clienteRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.solicitudRepository = solicitudRepository;
    }

    @Override
    public void run(String... args) {
        Cliente c1 = clienteRepository.save(new Cliente("Ana", "Torres Vega", "ana.torres@andina.pe",
                "987111222", "20512345678", "Comercial Andina SAC"));
        Cliente c2 = clienteRepository.save(new Cliente("Jorge", "Ramírez Soto", "jorge.ramirez@gmail.com",
                "945333444", "45781236", null));

        Tecnico t1 = tecnicoRepository.save(new Tecnico("Luis", "Huamán Torres", "luis.huaman@soporte.pe",
                "912345678", Especialidad.REDES));
        tecnicoRepository.save(new Tecnico("Carla", "Mendoza Ríos", "carla.mendoza@soporte.pe",
                "923456789", Especialidad.SOFTWARE));
        Tecnico t3 = tecnicoRepository.save(new Tecnico("Pedro", "Salas León", "pedro.salas@soporte.pe",
                "934567890", Especialidad.HARDWARE));

        solicitudRepository.save(new Solicitud("Sin acceso a internet",
                "Ninguna PC de la oficina de ventas tiene conexión a internet desde esta mañana.",
                Prioridad.CRITICA, c1.getId()));

        Solicitud enProceso = new Solicitud("Error al abrir el sistema contable",
                "El sistema contable muestra 'licencia expirada' al iniciar sesión.",
                Prioridad.ALTA, c1.getId());
        enProceso.setTecnicoId(t1.getId());
        enProceso.setEstado(EstadoSolicitud.EN_PROCESO);
        solicitudRepository.save(enProceso);

        Solicitud resuelta = new Solicitud("Laptop no enciende",
                "La laptop personal no enciende aunque está conectada al cargador.",
                Prioridad.MEDIA, c2.getId());
        resuelta.setTecnicoId(t3.getId());
        resuelta.setEstado(EstadoSolicitud.RESUELTA);
        resuelta.setFechaCierre(LocalDateTime.now());
        solicitudRepository.save(resuelta);

        log.info("Datos de ejemplo cargados: {} clientes, {} técnicos, {} solicitudes",
                clienteRepository.count(), tecnicoRepository.count(), solicitudRepository.count());
    }
}
