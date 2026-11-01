package pe.edu.idat.soporte.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración de la API: levantan el contexto de Spring
 * y ejecutan peticiones HTTP simuladas contra los controladores.
 */
@SpringBootTest(properties = "app.datos-iniciales=false")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SolicitudControllerTest {

    @Autowired
    private MockMvc mvc;

    private static final String CLIENTE_JSON = """
            {"nombres":"Ana","apellidos":"Torres","email":"ana@test.pe",
             "telefono":"987654321","documento":"12345678","empresa":"Andina SAC"}
            """;

    private static final String TECNICO_JSON = """
            {"nombres":"Luis","apellidos":"Huamán","email":"luis@test.pe",
             "telefono":"912345678","especialidad":"REDES"}
            """;

    private static final String SOLICITUD_JSON = """
            {"titulo":"Sin internet","descripcion":"No hay conexión en toda la oficina",
             "prioridad":"ALTA","clienteId":1}
            """;

    private void postOk(String url, String json) throws Exception {
        mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /solicitudes → 201 con Location y estado PENDIENTE")
    void registrar() throws Exception {
        postOk("/api/v1/clientes", CLIENTE_JSON);

        mvc.perform(post("/api/v1/solicitudes").contentType(MediaType.APPLICATION_JSON).content(SOLICITUD_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/solicitudes/1")))
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.datos.id").value(1))
                .andExpect(jsonPath("$.datos.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.datos.cliente.nombreCompleto").value("Ana Torres"));
    }

    @Test
    @DisplayName("POST /solicitudes con datos vacíos → 400 con errores por campo")
    void validaciones() throws Exception {
        mvc.perform(post("/api/v1/solicitudes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"\",\"descripcion\":\"corta\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exito").value(false))
                .andExpect(jsonPath("$.errores.titulo").exists())
                .andExpect(jsonPath("$.errores.descripcion").exists())
                .andExpect(jsonPath("$.errores.prioridad").value("La prioridad es obligatoria"))
                .andExpect(jsonPath("$.errores.clienteId").value("El id del cliente es obligatorio"));
    }

    @Test
    @DisplayName("Enum inválido → 400 indicando los valores permitidos")
    void enumInvalido() throws Exception {
        postOk("/api/v1/clientes", CLIENTE_JSON);
        mvc.perform(post("/api/v1/solicitudes").contentType(MediaType.APPLICATION_JSON)
                        .content(SOLICITUD_JSON.replace("ALTA", "URGENTE")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje", containsString("BAJA, MEDIA, ALTA, CRITICA")));
    }

    @Test
    @DisplayName("GET /solicitudes/99 → 404")
    void noEncontrada() throws Exception {
        mvc.perform(get("/api/v1/solicitudes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Solicitud con id 99 no existe"))
                .andExpect(jsonPath("$.ruta").value("/api/v1/solicitudes/99"));
    }

    @Test
    @DisplayName("GET /solicitudes/abc → 400 por tipo de parámetro")
    void idNoNumerico() throws Exception {
        mvc.perform(get("/api/v1/solicitudes/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Email de cliente duplicado → 409")
    void clienteDuplicado() throws Exception {
        postOk("/api/v1/clientes", CLIENTE_JSON);
        mvc.perform(post("/api/v1/clientes").contentType(MediaType.APPLICATION_JSON).content(CLIENTE_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Flujo CRUD completo con asignación y cambio de estado")
    void flujoCompleto() throws Exception {
        postOk("/api/v1/clientes", CLIENTE_JSON);
        postOk("/api/v1/tecnicos", TECNICO_JSON);
        postOk("/api/v1/solicitudes", SOLICITUD_JSON);

        // PUT
        mvc.perform(put("/api/v1/solicitudes/1").contentType(MediaType.APPLICATION_JSON)
                        .content(SOLICITUD_JSON.replace("ALTA", "CRITICA")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.prioridad").value("CRITICA"));

        // Transición inválida → 422
        mvc.perform(patch("/api/v1/solicitudes/1/estado").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"CERRADA\"}"))
                .andExpect(status().isUnprocessableEntity());

        // Asignar técnico → EN_PROCESO
        mvc.perform(patch("/api/v1/solicitudes/1/tecnico").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tecnicoId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.estado").value("EN_PROCESO"))
                .andExpect(jsonPath("$.datos.tecnico.id").value(1));

        // No se puede eliminar al cliente con solicitud activa → 422
        mvc.perform(delete("/api/v1/clientes/1"))
                .andExpect(status().isUnprocessableEntity());

        // Resolver
        mvc.perform(patch("/api/v1/solicitudes/1/estado").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"RESUELTA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.datos.fechaCierre").exists());

        // Filtro por estado
        mvc.perform(get("/api/v1/solicitudes").param("estado", "RESUELTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));

        // Ruta jerárquica
        mvc.perform(get("/api/v1/clientes/1/solicitudes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));

        // DELETE
        mvc.perform(delete("/api/v1/solicitudes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Solicitud 1 eliminada correctamente"));
        mvc.perform(get("/api/v1/solicitudes/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Ruta inexistente → 404 con formato de error estándar")
    void rutaInexistente() throws Exception {
        mvc.perform(get("/api/v1/no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exito").value(false));
    }
}
