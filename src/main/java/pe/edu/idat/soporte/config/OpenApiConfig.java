package pe.edu.idat.soporte.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la documentación Swagger / OpenAPI.
 * UI disponible en http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI soporteTecnicoOpenApi() {
        return new OpenAPI().info(new Info()
                .title("API de Solicitudes de Soporte Técnico")
                .version("1.0.0")
                .description("""
                        API RESTful para registrar, consultar, actualizar y eliminar solicitudes de \
                        soporte técnico, clientes y técnicos. Los datos se almacenan en memoria \
                        (colecciones Java) mientras la aplicación está en ejecución.""")
                .contact(new Contact().name("Equipo Backend - IDAT"))
                .license(new License().name("Uso académico")));
    }
}
