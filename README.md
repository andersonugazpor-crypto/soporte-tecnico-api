# API de Solicitudes de Soporte Técnico

API RESTful desarrollada con **Java 17 y Spring Boot 3** para registrar, consultar, actualizar y eliminar solicitudes de soporte técnico, junto con los clientes que las reportan y los técnicos que las atienden.

Los datos se guardan **en memoria** (colecciones `ConcurrentHashMap`) mientras la aplicación está en ejecución; no se necesita base de datos.

> Proyecto académico — IDAT, unidad didáctica *Desarrollo de los Componentes del Negocio*.

---

## Contenido

1. [Tecnologías](#tecnologías)
2. [Requisitos](#requisitos)
3. [Instalación y ejecución](#instalación-y-ejecución)
4. [Estructura del proyecto](#estructura-del-proyecto)
5. [Arquitectura](#arquitectura)
6. [Modelo de dominio y reglas de negocio](#modelo-de-dominio-y-reglas-de-negocio)
7. [Endpoints](#endpoints)
8. [Ejemplos de uso](#ejemplos-de-uso)
9. [Manejo de errores](#manejo-de-errores)
10. [Pruebas](#pruebas)
11. [Equipo](#equipo)

---

## Tecnologías

| Tecnología | Uso |
|---|---|
| Java 17 | Lenguaje (records, `switch` con flechas, text blocks) |
| Spring Boot 3.5 (Web) | API REST |
| Jakarta Bean Validation | Validación de datos de entrada (`@Valid`, `@NotNull`, `@NotBlank`, `@Email`, `@Pattern`, `@Size`, `@Positive`) |
| springdoc-openapi 2.8 | Documentación Swagger UI |
| JUnit 5 + MockMvc + AssertJ | Pruebas unitarias y de integración |
| Postman | Pruebas funcionales (colección incluida) |
| Maven | Gestión de dependencias y construcción |

## Requisitos

- **JDK 17** o superior (`java -version`)
- **Maven 3.8+** (`mvn -version`) — o un IDE que lo incluya (IntelliJ IDEA, Eclipse, VS Code con Extension Pack for Java)
- **Postman** (opcional, para las pruebas funcionales)

## Instalación y ejecución

### Opción 1: línea de comandos

```bash
# 1. Clonar el repositorio
git clone https://github.com/<usuario>/soporte-tecnico-api.git
cd soporte-tecnico-api

# 2. Ejecutar las pruebas automatizadas
mvn test

# 3. Levantar la aplicación
mvn spring-boot:run
```

O bien, generar el `.jar` y ejecutarlo:

```bash
mvn clean package
java -jar target/soporte-tecnico-api-1.0.0.jar
```

### Opción 2: IntelliJ IDEA

1. `File > Open` y seleccionar la carpeta del proyecto (se importa como proyecto Maven).
2. Esperar a que descargue las dependencias.
3. Ejecutar la clase `SoporteTecnicoApiApplication`.

### Verificar que está funcionando

| Recurso | URL |
|---|---|
| API base | http://localhost:8080/api/v1 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Especificación OpenAPI (JSON) | http://localhost:8080/v3/api-docs |

Al iniciar se cargan **datos de ejemplo** (2 clientes, 3 técnicos y 3 solicitudes). Para arrancar vacío, cambiar en `src/main/resources/application.properties`:

```properties
app.datos-iniciales=false
```

> Como los datos están en memoria, se pierden al detener la aplicación.

## Estructura del proyecto

```
soporte-tecnico-api/
├── pom.xml
├── README.md
├── postman/
│   └── Soporte-Tecnico-API.postman_collection.json   ← 35 peticiones con tests
└── src/
    ├── main/
    │   ├── java/pe/edu/idat/soporte/
    │   │   ├── SoporteTecnicoApiApplication.java    ← clase principal
    │   │   ├── config/        OpenApiConfig, DatosIniciales
    │   │   ├── controller/    ClienteController, TecnicoController, SolicitudController
    │   │   ├── dto/
    │   │   │   ├── request/   ClienteRequest, TecnicoRequest, SolicitudRequest,
    │   │   │   │              CambioEstadoRequest, AsignacionTecnicoRequest
    │   │   │   └── response/  ApiResponse, ErrorResponse, ClienteResponse,
    │   │   │                  TecnicoResponse, SolicitudResponse
    │   │   ├── exception/     GlobalExceptionHandler (@RestControllerAdvice) + excepciones propias
    │   │   ├── mapper/        Conversión entidad ↔ DTO
    │   │   ├── model/         EntidadBase, Persona, Cliente, Tecnico, Solicitud
    │   │   │   └── enums/     Prioridad, Especialidad, EstadoSolicitud
    │   │   ├── repository/    CrudRepository (interfaz), InMemoryRepository (genérico)
    │   │   │                  y repositorios concretos
    │   │   └── service/       Interfaces de servicio
    │   │       └── impl/      Implementaciones con la lógica de negocio
    │   └── resources/
    │       └── application.properties
    └── test/java/pe/edu/idat/soporte/
        ├── controller/SolicitudControllerTest.java  ← integración (MockMvc)
        └── service/SolicitudServiceImplTest.java    ← unitarias
```

## Arquitectura

Arquitectura en capas; cada capa solo conoce a la de abajo y se comunica mediante interfaces y DTOs.

```
Cliente HTTP (Postman / Swagger)
          │  JSON
          ▼
┌─────────────────────┐   valida con @Valid, arma ResponseEntity
│   controller        │   y códigos HTTP. No contiene lógica de negocio.
└─────────┬───────────┘
          │  DTO request / response
          ▼
┌─────────────────────┐   interfaz + implementación (@Service).
│   service           │   Reglas de negocio: unicidad, transiciones
└─────────┬───────────┘   de estado, integridad entre entidades.
          │  entidades del modelo
          ▼
┌─────────────────────┐   CrudRepository<T> → InMemoryRepository<T>
│   repository        │   ConcurrentHashMap + AtomicLong (simula una tabla
└─────────┬───────────┘   con id autoincremental y auditoría de fechas).
          ▼
     model (entidades)

exception/GlobalExceptionHandler intercepta cualquier error de las capas
y lo convierte en una respuesta JSON uniforme con su código HTTP.
```

**Decisiones de diseño**

- **Herencia y abstracción:** `EntidadBase` (id y fechas) → `Persona` (datos de contacto) → `Cliente` / `Tecnico`. `Solicitud` hereda de `EntidadBase`.
- **Repositorio genérico:** `InMemoryRepository<T extends EntidadBase>` implementa el CRUD una sola vez para todas las entidades; los repositorios concretos solo agregan búsquedas propias.
- **DTOs con `record`:** las entidades nunca se exponen directamente; la entrada se valida en los DTOs de request y la salida usa DTOs de response.
- **Inyección por constructor** y dependencia de interfaces (`ClienteService`, `CrudRepository`): si mañana se cambia a una base de datos, solo cambia la implementación del repositorio.
- **Concurrencia:** `ConcurrentHashMap` y `AtomicLong` evitan ids duplicados cuando llegan varias peticiones a la vez.

## Modelo de dominio y reglas de negocio

| Entidad | Atributos principales |
|---|---|
| **Cliente** | id, nombres, apellidos, email (único), teléfono, documento DNI/RUC (único), empresa |
| **Técnico** | id, nombres, apellidos, email (único), teléfono, especialidad (`HARDWARE`, `SOFTWARE`, `REDES`, `SEGURIDAD`), activo |
| **Solicitud** | id, título, descripción, prioridad (`BAJA`, `MEDIA`, `ALTA`, `CRITICA`), estado, clienteId, tecnicoId, fechaCreacion, fechaActualizacion, fechaCierre |

**Ciclo de vida de una solicitud**

```
PENDIENTE  ──► EN_PROCESO ──► RESUELTA ──► CERRADA
    │              │             │
    ▼              ▼             └──► EN_PROCESO (reapertura)
CANCELADA      CANCELADA
```

**Reglas**

1. Toda solicitud nueva se crea en estado `PENDIENTE` y debe pertenecer a un cliente existente.
2. Al asignar un técnico a una solicitud `PENDIENTE`, pasa automáticamente a `EN_PROCESO`. El técnico debe estar activo.
3. No se puede pasar a `EN_PROCESO` sin técnico asignado.
4. Solo se permiten las transiciones del diagrama; cualquier otra devuelve **422**.
5. Al pasar a `RESUELTA` o `CANCELADA` se registra `fechaCierre`; si se reabre, se limpia.
6. Una solicitud `CERRADA` o `CANCELADA` ya no puede editarse ni reasignarse.
7. No se puede eliminar un cliente con solicitudes `PENDIENTE` o `EN_PROCESO`. Si se elimina, su historial se elimina con él (como un `ON DELETE CASCADE`).
8. No se puede eliminar ni desactivar un técnico con solicitudes en curso. Si se elimina, sus solicitudes finalizadas quedan sin técnico (como un `ON DELETE SET NULL`).
9. Email y documento de cliente, y email de técnico, no se pueden repetir (**409**).

## Endpoints

Base: `http://localhost:8080/api/v1`

### Solicitudes

| Método | Ruta | Descripción | Éxito | Errores |
|---|---|---|---|---|
| GET | `/solicitudes` | Listar. Filtros opcionales: `estado`, `prioridad`, `clienteId`, `tecnicoId` | 200 | 400 |
| GET | `/solicitudes/{id}` | Obtener por id | 200 | 400, 404 |
| POST | `/solicitudes` | Registrar | 201 + `Location` | 400, 404 |
| PUT | `/solicitudes/{id}` | Actualizar título, descripción, prioridad, cliente | 200 | 400, 404, 422 |
| PATCH | `/solicitudes/{id}/estado` | Cambiar estado | 200 | 400, 404, 422 |
| PATCH | `/solicitudes/{id}/tecnico` | Asignar técnico | 200 | 400, 404, 422 |
| DELETE | `/solicitudes/{id}` | Eliminar | 200 | 404 |

### Clientes

| Método | Ruta | Descripción | Éxito | Errores |
|---|---|---|---|---|
| GET | `/clientes` | Listar | 200 | — |
| GET | `/clientes/{id}` | Obtener por id | 200 | 404 |
| GET | `/clientes/{id}/solicitudes` | Solicitudes del cliente | 200 | 404 |
| POST | `/clientes` | Registrar | 201 + `Location` | 400, 409 |
| PUT | `/clientes/{id}` | Actualizar | 200 | 400, 404, 409 |
| DELETE | `/clientes/{id}` | Eliminar | 200 | 404, 422 |

### Técnicos

| Método | Ruta | Descripción | Éxito | Errores |
|---|---|---|---|---|
| GET | `/tecnicos` | Listar. Filtro opcional: `especialidad` | 200 | 400 |
| GET | `/tecnicos/{id}` | Obtener por id | 200 | 404 |
| GET | `/tecnicos/{id}/solicitudes` | Solicitudes asignadas | 200 | 404 |
| POST | `/tecnicos` | Registrar | 201 + `Location` | 400, 409 |
| PUT | `/tecnicos/{id}` | Actualizar | 200 | 400, 404, 409, 422 |
| DELETE | `/tecnicos/{id}` | Eliminar | 200 | 404, 422 |

### Validaciones de entrada

| DTO | Campo | Regla |
|---|---|---|
| SolicitudRequest | titulo | obligatorio, 5–100 caracteres |
| | descripcion | obligatoria, 10–1000 caracteres |
| | prioridad | obligatoria, `BAJA` \| `MEDIA` \| `ALTA` \| `CRITICA` |
| | clienteId | obligatorio, positivo |
| ClienteRequest | nombres, apellidos | obligatorios, 2–60 caracteres |
| | email | obligatorio, formato email |
| | telefono | obligatorio, 9 dígitos empezando en 9 |
| | documento | obligatorio, DNI (8 dígitos) o RUC (11 dígitos) |
| | empresa | opcional, máx. 100 caracteres |
| TecnicoRequest | nombres, apellidos, email, telefono | igual que cliente |
| | especialidad | obligatoria, `HARDWARE` \| `SOFTWARE` \| `REDES` \| `SEGURIDAD` |
| | activo | opcional (por defecto `true`) |
| CambioEstadoRequest | estado | obligatorio |
| AsignacionTecnicoRequest | tecnicoId | obligatorio, positivo |

## Ejemplos de uso

### Registrar una solicitud

```http
POST /api/v1/solicitudes
Content-Type: application/json

{
  "titulo": "La impresora no imprime",
  "descripcion": "La impresora del área de ventas muestra error de atasco sin papel atascado.",
  "prioridad": "ALTA",
  "clienteId": 1
}
```

Respuesta `201 Created` (header `Location: http://localhost:8080/api/v1/solicitudes/4`):

```json
{
  "exito": true,
  "mensaje": "Solicitud registrada correctamente",
  "datos": {
    "id": 4,
    "titulo": "La impresora no imprime",
    "descripcion": "La impresora del área de ventas muestra error de atasco sin papel atascado.",
    "prioridad": "ALTA",
    "estado": "PENDIENTE",
    "cliente": { "id": 1, "nombreCompleto": "Ana Torres Vega", "email": "ana.torres@andina.pe" },
    "tecnico": null,
    "fechaCreacion": "2026-11-01T14:05:12.481",
    "fechaActualizacion": "2026-11-01T14:05:12.481",
    "fechaCierre": null
  },
  "timestamp": "2026-11-01T14:05:12.483"
}
```

### Listar con filtros

```http
GET /api/v1/solicitudes?estado=PENDIENTE&prioridad=CRITICA
```

```json
{
  "exito": true,
  "mensaje": "Listado de solicitudes",
  "datos": [ { "id": 1, "titulo": "Sin acceso a internet", "estado": "PENDIENTE", "prioridad": "CRITICA", "...": "..." } ],
  "total": 1,
  "timestamp": "2026-11-01T14:06:00.120"
}
```

### Asignar técnico y cambiar estado

```http
PATCH /api/v1/solicitudes/4/tecnico
Content-Type: application/json

{ "tecnicoId": 1 }
```

```http
PATCH /api/v1/solicitudes/4/estado
Content-Type: application/json

{ "estado": "RESUELTA" }
```

### Con cURL

```bash
curl -X POST http://localhost:8080/api/v1/solicitudes \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Sin internet","descripcion":"No hay conexión en toda la oficina","prioridad":"ALTA","clienteId":1}'
```

## Manejo de errores

Todos los errores pasan por `GlobalExceptionHandler` (`@RestControllerAdvice`) y tienen la misma estructura:

```json
{
  "exito": false,
  "status": 400,
  "error": "Bad Request",
  "mensaje": "Los datos enviados no son válidos",
  "ruta": "/api/v1/solicitudes",
  "errores": {
    "titulo": "El título es obligatorio",
    "prioridad": "La prioridad es obligatoria",
    "clienteId": "El id del cliente es obligatorio"
  },
  "timestamp": "2026-11-01T14:10:44.051"
}
```

| Código | Cuándo ocurre | Ejemplo de mensaje |
|---|---|---|
| 400 | Falla una validación (`@Valid`) | `errores` con un mensaje por campo |
| 400 | JSON mal formado o valor de enum inválido | `Valor 'URGENTE' no válido para 'prioridad'. Valores permitidos: BAJA, MEDIA, ALTA, CRITICA` |
| 400 | Parámetro de tipo incorrecto (`/solicitudes/abc`) | `El parámetro 'id' tiene un valor inválido: 'abc'` |
| 404 | El recurso no existe | `Solicitud con id 99 no existe` |
| 404 | La ruta no existe | `La ruta solicitada no existe` |
| 405 | Método HTTP no permitido en la ruta | `El método PATCH no está permitido en esta ruta` |
| 409 | Email o documento duplicado | `Ya existe un cliente con el email ana@test.pe` |
| 415 | Content-Type distinto de JSON | `El Content-Type debe ser application/json` |
| 422 | Se viola una regla de negocio | `No se puede pasar de PENDIENTE a CERRADA. Transiciones permitidas desde PENDIENTE: [EN_PROCESO, CANCELADA]` |
| 500 | Error inesperado | `Ocurrió un error interno. Intente nuevamente más tarde` |

## Pruebas

### Pruebas automatizadas (JUnit)

```bash
mvn test
```

- `SolicitudServiceImplTest` — 8 pruebas unitarias de la lógica de negocio (registro, transiciones de estado, asignación, filtros, eliminación).
- `SolicitudControllerTest` — 8 pruebas de integración con MockMvc (códigos HTTP 201/400/404/409/422, header `Location`, flujo CRUD completo, rutas jerárquicas).

### Pruebas funcionales con Postman

La colección `postman/Soporte-Tecnico-API.postman_collection.json` tiene **35 peticiones con tests** que verifican el código de estado, la estructura de la respuesta y los datos devueltos. Cubre el CRUD de las tres entidades y todos los casos de error.

1. Levantar la API (`mvn spring-boot:run`).
2. En Postman: `Import` → seleccionar el archivo de la colección.
3. Clic derecho en la colección → **Run collection** → **Run**.
4. Las peticiones se ejecutan en orden y comparten ids mediante variables de colección (`clienteId`, `tecnicoId`, `solicitudId`). La variable `baseUrl` apunta a `http://localhost:8080`.

También se puede ejecutar desde consola con Newman:

```bash
npx newman run postman/Soporte-Tecnico-API.postman_collection.json
```

## Equipo

| Integrante | Rol | Responsabilidades |
|---|---|---|
| _Nombre 1_ | Líder técnico / Arquitectura | Estructura del proyecto, modelo de dominio, repositorio en memoria |
| _Nombre 2_ | Desarrollador backend | Servicios, reglas de negocio, controladores y validaciones |
| _Nombre 3_ | QA / Documentación | Manejo de errores, pruebas JUnit y Postman, Swagger y README |
