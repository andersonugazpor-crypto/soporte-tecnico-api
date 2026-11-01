package pe.edu.idat.soporte;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la API RESTful de Soporte Técnico.
 * <p>
 * La aplicación gestiona clientes, técnicos y solicitudes de soporte
 * usando colecciones en memoria (sin base de datos).
 */
@SpringBootApplication
public class SoporteTecnicoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SoporteTecnicoApiApplication.class, args);
    }
}
