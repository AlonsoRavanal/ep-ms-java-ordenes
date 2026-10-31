# EventPass — Microservicio de Órdenes

Microservicio REST desarrollado con **Java 21**, **Spring Boot 4.1.1**, **Spring Web MVC**, **Spring Data JPA** y una base de datos relacional **H2 persistente en archivo**. Permite registrar, consultar, actualizar y eliminar órdenes de EventPass.

Este repositorio forma parte del proyecto académico **EventPass**, desarrollado para la asignatura **Java: Diseño y Construcción de Soluciones Nativas en Nube (JVY0101)**.

> **Alcance de la implementación adjunta:** el servicio administra órdenes mediante operaciones CRUD y almacena los identificadores del comprador y del evento. **No consulta otros microservicios para comprobar que esos identificadores existan, no descuenta aforo ni emite tickets automáticamente.** El estado de una orden puede modificarse mediante `PUT`, pero cambiarlo a `EMITIDA` no genera entradas en el microservicio de tickets por sí solo.

## 1. Funcionalidades

- Crear órdenes de compra asociadas mediante identificadores a un comprador y un evento.
- Consultar todas las órdenes o una orden mediante su ID.
- Actualizar los datos y el estado de una orden existente.
- Eliminar órdenes.
- Validar que `compradorId`, `eventoId` y `cantidad` sean obligatorios y mayores que cero.
- Registrar automáticamente la fecha de creación mediante `@PrePersist`.
- Asignar el estado inicial `ACEPTADA_PENDIENTE_EMISION` cuando no se proporciona uno al crear una orden.
- Persistir los datos en una base H2 local mediante JPA/Hibernate.
- Exponer endpoints REST que pueden probarse con Postman.

## 2. Tecnologías y dependencias

| Componente | Función |
|---|---|
| Java 21 | Lenguaje y versión de Java configurada en el proyecto |
| Spring Boot 4.1.1 | Framework principal |
| Spring Web MVC | Implementación de la API REST |
| Spring Data JPA / Hibernate | Persistencia de datos y mapeo de la entidad |
| H2 Database | Base de datos relacional local en modo archivo |
| Jakarta Bean Validation | Validaciones con `@NotNull`, `@Positive` y `@Valid` |
| Maven / Maven Wrapper | Dependencias, compilación, pruebas y empaquetado |
| JUnit / Spring Boot Test | Prueba de carga del contexto de Spring Boot |

La configuración real de las dependencias se encuentra en `pom.xml`.

## 3. Arquitectura del proyecto

El código está organizado en capas, siguiendo el patrón **controller → service → repository → database**:

```text
ep-ms-java-ordenes/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
└── src/
    ├── main/
    │   ├── java/com/alonsoravanalrubenvelazquez/ep_ms_java_ordenes/
    │   │   ├── EpMsJavaOrdenesApplication.java
    │   │   ├── controller/
    │   │   │   └── OrdenRestController.java
    │   │   ├── service/
    │   │   │   ├── OrdenService.java
    │   │   │   └── OrdenServiceImpl.java
    │   │   ├── repository/
    │   │   │   └── OrdenRepository.java
    │   │   └── model/
    │   │       └── Orden.java
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/alonsoravanalrubenvelazquez/ep_ms_java_ordenes/
            └── EpMsJavaOrdenesApplicationTests.java
```

**Responsabilidades:**

- **Controller (`OrdenRestController`):** define los endpoints y las respuestas HTTP.
- **Service (`OrdenService` y `OrdenServiceImpl`):** implementa la lógica CRUD, las validaciones adicionales y las transacciones.
- **Repository (`OrdenRepository`):** extiende `JpaRepository<Orden, Long>` para acceder a la base de datos.
- **Model (`Orden`):** define la entidad JPA y sus restricciones.

La entidad `Orden` almacena los IDs de comprador y evento como campos numéricos. **En este microservicio no hay relaciones JPA `@ManyToOne` con las entidades de otros microservicios.**

## 4. Modelo de datos: Orden

La entidad está mapeada mediante `@Entity` y `@Table(name = "ordenes")`.

| Campo | Tipo Java | Descripción |
|---|---|---|
| `id` | `Long` | Identificador de la orden, autogenerado |
| `compradorId` | `Long` | Identificador del comprador; obligatorio y positivo |
| `eventoId` | `Long` | Identificador del evento; obligatorio y positivo |
| `cantidad` | `Integer` | Cantidad de entradas solicitadas; obligatoria y positiva |
| `fechaCreacion` | `LocalDateTime` | Fecha y hora asignadas al crear la orden |
| `estado` | `EstadoOrden` | Estado de la orden, almacenado como texto mediante `@Enumerated(EnumType.STRING)` |

### Estados posibles

```text
ACEPTADA_PENDIENTE_EMISION
EMITIDA
RECHAZADA_SIN_CUPO
ERROR_EMISION
```

Cuando se crea una orden y no se indica el estado, se utiliza `ACEPTADA_PENDIENTE_EMISION`. El método de actualización conserva la fecha de creación almacenada y actualiza los campos `compradorId`, `eventoId`, `cantidad` y `estado`.

> **Nota:** el código adjunto no implementa un flujo automático entre esos estados ni verifica la disponibilidad de cupos. El estado puede cambiarse mediante la API.

## 5. Requisitos previos

- **JDK 21** instalado. Comprueba la versión con `java -version`.
- **Git**, para clonar el repositorio.
- Acceso a Internet durante la primera descarga de dependencias de Maven.
- **Postman**, o cualquier cliente HTTP, para probar la API.

El proyecto incluye **Maven Wrapper** (`mvnw.cmd` en Windows y `mvnw` en Linux/macOS); no es necesario instalar Maven globalmente.

## 6. Clonar y ejecutar localmente

### 6.1. Clonar el repositorio

En una terminal de **Windows (CMD)**:

```cmd
git clone https://github.com/AlonsoRavanal/ep-ms-java-ordenes.git
cd ep-ms-java-ordenes
```

### 6.2. Preparar la carpeta de la base de datos

Desde la raíz del proyecto:

```cmd
if not exist data mkdir data
```

H2 utilizará la carpeta `data/` para guardar su base de datos local.

### 6.3. Ejecutar Spring Boot

En **Windows (CMD)**:

```cmd
mvnw.cmd spring-boot:run
```

En **Linux/macOS**:

```bash
mkdir -p data
./mvnw spring-boot:run
```

Una vez iniciado el servicio, la URL base será:

**`http://localhost:8083`**

Para detenerlo, utiliza `Ctrl + C` en la terminal.

## 7. Configuración y conexión con H2

La configuración declarada en `src/main/resources/application.properties` es:

```properties
spring.application.name=ep-ms-java-ordenes

server.port=8083

spring.datasource.url=jdbc:h2:file:./data/bdordenes
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

- **Puerto HTTP:** `8083`.
- **Base de datos:** H2 en modo archivo, con URL JDBC `jdbc:h2:file:./data/bdordenes`.
- **Archivo local esperado:** `data/bdordenes.mv.db`.
- **JPA/Hibernate:** `ddl-auto=update` permite actualizar el esquema a partir de la entidad.
- **Registro SQL:** `show-sql=true` muestra consultas SQL durante las operaciones.

La ruta H2 es relativa al directorio desde el que se inicia la aplicación; por ello se recomienda ejecutar los comandos desde la raíz del proyecto.

### Consultar la base de datos desde H2 Console

Con la aplicación ejecutándose, abre:

**`http://localhost:8083/h2-console`**

Configura la conexión de la siguiente manera:

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:file:./data/bdordenes` |
| User Name | `sa` |
| Password | *(vacía)* |

Para comprobar que las órdenes se guardaron, puedes consultar:

```sql
SELECT * FROM ORDENES;
```

> **Solo para desarrollo local:** la consola H2 está habilitada y el usuario `sa` no tiene contraseña. No se debe utilizar esta configuración de demostración tal cual en producción.

## 8. Endpoints REST

**URL base:** `http://localhost:8083/api/ordenes`

| Método | Endpoint | Descripción | Respuesta esperada |
|---|---|---|---|
| `GET` | `/api/ordenes` | Listar todas las órdenes | `200 OK` |
| `GET` | `/api/ordenes/{id}` | Consultar una orden por ID | `200 OK` o `404 Not Found` |
| `POST` | `/api/ordenes` | Crear una orden | `201 Created` o `400 Bad Request` |
| `PUT` | `/api/ordenes/{id}` | Actualizar una orden | `200 OK`, `400 Bad Request` o `404 Not Found` |
| `DELETE` | `/api/ordenes/{id}` | Eliminar una orden | `204 No Content` o `404 Not Found` |

Para las solicitudes `POST` y `PUT`, utiliza **Body → raw → JSON** en Postman y el encabezado `Content-Type: application/json`.

## 9. Pruebas paso a paso con Postman

Los siguientes ejemplos utilizan identificadores de prueba. En la implementación actual, **no se comprueba con una llamada a otros microservicios si `compradorId` o `eventoId` existen realmente**.

### Prueba 1. Listar las órdenes

**GET** `http://localhost:8083/api/ordenes`

**Resultado esperado:** `200 OK`, con una lista JSON de órdenes; puede estar vacía si todavía no se han registrado datos.

### Prueba 2. Crear una orden válida

**POST** `http://localhost:8083/api/ordenes`

```json
{
  "compradorId": 1,
  "eventoId": 501,
  "cantidad": 3
}
```

**Resultado esperado:** `201 Created`. El servidor asignará `id`, `fechaCreacion` y, al no indicar un estado, `ACEPTADA_PENDIENTE_EMISION`.

Ejemplo **ilustrativo** de respuesta (los valores reales de ID y fecha serán distintos):

```json
{
  "id": 1,
  "compradorId": 1,
  "eventoId": 501,
  "cantidad": 3,
  "fechaCreacion": "2026-10-30T20:00:00",
  "estado": "ACEPTADA_PENDIENTE_EMISION"
}
```

Anota el **ID real** que devuelva Postman para las siguientes solicitudes.

### Prueba 3. Buscar una orden por ID

**GET** `http://localhost:8083/api/ordenes/1`

Sustituye `1` por el ID de la orden creada. **Resultado esperado:** `200 OK`. Si el ID no existe, `404 Not Found`.

### Prueba 4. Actualizar una orden

**PUT** `http://localhost:8083/api/ordenes/1`

```json
{
  "compradorId": 1,
  "eventoId": 501,
  "cantidad": 2,
  "estado": "EMITIDA"
}
```

**Resultado esperado:** `200 OK`, con los datos actualizados. El código conserva la fecha original de creación.

**Atención:** establecer `"estado": "EMITIDA"` **solo actualiza el estado en este microservicio**; no emite tickets ni se comunica automáticamente con `ep-ms-java-tickets`.

En las actualizaciones se recomienda enviar el estado deseado explícitamente, porque el método `update()` asigna a la entidad el valor de `estado` recibido, incluso si es `null`.

### Prueba 5. Rechazar una cantidad negativa

**POST** `http://localhost:8083/api/ordenes`

```json
{
  "compradorId": 1,
  "eventoId": 501,
  "cantidad": -5
}
```

**Resultado esperado:** `400 Bad Request`. La regla `@Positive` y la validación adicional del servicio impiden cantidades negativas o cero.

### Prueba 6. Rechazar valores nulos

**POST** `http://localhost:8083/api/ordenes`

```json
{
  "compradorId": null,
  "eventoId": 501,
  "cantidad": 3
}
```

**Resultado esperado:** `400 Bad Request`, debido a `@NotNull`. Puedes repetir la prueba colocando `null` en `eventoId` o `cantidad`.

### Prueba 7. Rechazar identificadores no positivos

**POST** `http://localhost:8083/api/ordenes`

```json
{
  "compradorId": 0,
  "eventoId": -10,
  "cantidad": 3
}
```

**Resultado esperado:** `400 Bad Request`, por las restricciones de valores positivos.

### Prueba 8. Eliminar una orden

**DELETE** `http://localhost:8083/api/ordenes/1`

Sustituye `1` por un ID existente. **Resultado esperado:** `204 No Content`.

Después realiza un **GET** a la misma URL: el resultado esperado es `404 Not Found`.

### Comprobar la persistencia

1. Crea una orden válida con Postman.
2. Abre `http://localhost:8083/h2-console` y ejecuta `SELECT * FROM ORDENES;`.
3. Comprueba que el registro esté en la tabla.
4. Detén el servicio y vuelve a iniciarlo desde la misma carpeta.
5. Consulta nuevamente las órdenes: al utilizar H2 en modo archivo, los datos deberían permanecer disponibles.

## 10. Compilación, pruebas y empaquetado con Maven

Desde la raíz del proyecto, en Windows CMD:

```cmd
mvnw.cmd clean
mvnw.cmd test
mvnw.cmd package
```

También puedes ejecutar el ciclo completo de compilación, pruebas y empaquetado mediante:

```cmd
mvnw.cmd clean package
```

Si las tareas se completan correctamente, Maven genera el JAR en `target/`. Con la versión y el identificador declarados en el `pom.xml`, el nombre esperado es:

```text
target/ep-ms-java-ordenes-0.0.1-SNAPSHOT.jar
```

Puedes ejecutarlo en Windows mediante:

```cmd
java -jar target\ep-ms-java-ordenes-0.0.1-SNAPSHOT.jar
```

---

**Proyecto académico EventPass — Microservicio de Órdenes**  
**Asignatura:** Java: Diseño y Construcción de Soluciones Nativas en Nube (JVY0101).
