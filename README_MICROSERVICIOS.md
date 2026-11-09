# Tramigo – nuevos microservicios

Mismo stack que `usuarios-service`: Java 25, Spring Boot 4.1.1, Spring Cloud 2025.1.3, Maven (`mvnw`),
MySQL (una BD por servicio), registro en Eureka (`localhost:8761`), respuestas `{success, message, ...}`.

| Servicio | Puerto | Base de datos | Qué hace | Sale de |
|---|---|---|---|---|
| usuarios-service (ya existía) | 8081 | db_usuarios | Registro, login, perfil, personas | `Usuario`, `Persona`, `AuthController`, `PerfilController` |
| **documentos-service** | 8082 | db_documentos | Subir, listar, descargar y borrar documentos del usuario | `DocumentoController`, `FileStorageService` |
| **soporte-service** | 8083 | db_soporte | Tickets de contacto/soporte y su estado | `SoporteController`, `Soporte` |
| **notificaciones-service** | 8084 | db_notificaciones | Envío de correo a soporte + historial (ENVIADA / FALLIDA / OMITIDA) | `EmailService` |
| **tramites-service** | 8085 | db_tramites | Catálogo de trámites y bancos (hoy fijo en `categorias.html` / `categorias_bancos.html`) | `HomeController` + plantillas |
| **seguimiento-service** | 8086 | db_seguimiento | Registro de solicitudes, consulta de estado e historial de cambios | `Solicitud`, `HistorialSeguimiento` |
| **api-gateway** | 8090 | – | Punto único de entrada, rutea por Eureka | – |

## Comunicación entre servicios
- `documentos-service` → `usuarios-service`: valida que el `usuarioId` exista antes de aceptar un archivo.
- `soporte-service` → `notificaciones-service`: avisa por correo al crear un ticket (si falla, el ticket no se pierde).
- Las llamadas usan `RestClient` con balanceo (`http://nombre-del-servicio/...`) vía Eureka.

## Endpoints
- **documentos**: `GET /api/documentos/usuario/{usuarioId}` · `GET /api/documentos/{id}?usuarioId=` · `GET /api/documentos/{id}/archivo?usuarioId=` · `POST /api/documentos` (multipart: `usuarioId`, `archivo`, `nombre`) · `DELETE /api/documentos/{id}?usuarioId=`
- **soporte**: `POST /api/soporte` · `GET /api/soporte?estado=` · `GET /api/soporte/{id}` · `GET /api/soporte/correo?correo=` · `PUT /api/soporte/{id}/estado` (Pendiente / En proceso / Resuelto)
- **notificaciones** (interno): `POST /api/notificaciones/contacto` · `GET /api/notificaciones?estado=` · `GET /api/notificaciones/{id}`
- **tramites**: `GET /api/tramites?categoria=&q=&grupo=` · `GET /api/tramites/categorias` · `GET /api/tramites/{slug}` · `POST` / `PUT /{slug}` / `DELETE /{slug}`
- **seguimiento**: `POST /api/seguimiento/solicitudes` · `GET /api/seguimiento/solicitudes?usuarioId=` · `GET /api/seguimiento/solicitudes/{id}` · `PUT /api/seguimiento/solicitudes/{id}/estado` · `GET /api/seguimiento/solicitudes/{id}/historial`
  (`?grupo=cuenta-bancaria` devuelve los 5 bancos; el catálogo se carga solo si la tabla está vacía)
- **gateway (8090)** reenvía `/api/auth`, `/api/usuarios`, `/api/personas`, `/uploads`, `/api/documentos`, `/api/soporte`, `/api/tramites`, `/api/seguimiento`. `notificaciones-service` no se expone.

### Ejemplo rápido de seguimiento

Crear una solicitud:
```json
POST /api/seguimiento/solicitudes
{
  "usuarioId": 1,
  "tramiteId": 3,
  "titulo": "Renovación de INE",
  "descripcion": "Solicitud iniciada desde Tramigo"
}
```

Cambiar su estado:
```json
PUT /api/seguimiento/solicitudes/1/estado
{
  "estado": "EN_REVISION",
  "comentario": "Se revisaron los documentos"
}
```

Cada cambio de estado queda guardado en el historial. Los IDs de usuario y trámite se guardan como referencias numéricas; todavía no se valida su existencia llamando a los otros microservicios.

## Cómo arrancar
1. MySQL en `localhost:3306`.
2. `iniciar_todo.bat` (levanta Eureka, los servicios y al final el gateway), o cada uno con su `iniciar_<servicio>.bat`.
3. Variables de entorno opcionales: `DB_USERNAME`, `DB_PASSWORD`, y para correo `MAIL_USERNAME`, `MAIL_PASSWORD`, `SOPORTE_EMAIL`.
   Sin `MAIL_USERNAME` los correos quedan como `OMITIDA` en el historial (no truena nada).
4. Datos existentes del monolito: ver `migracion_datos.sql`.

## Pendiente / a tener en cuenta
- Ningún servicio tiene autenticación (igual que el monolito: `/admin/mensajes` estaba abierto). Antes de exponer el gateway, los endpoints de administración de soporte y de edición del catálogo deberían protegerse.
- Los servicios son solo REST; las pantallas Thymeleaf siguen en `tramigo-java` y habría que cambiarlas para que llamen a las APIs.

## Mejora inicial de integración MVC
- `tramigo-java` ahora usa `SoporteController` → `SoporteApiService` → `api-gateway` → `soporte-service` para crear y consultar tickets.
- La pantalla de contacto deja de guardar los tickets directamente en la base de datos `logintramigo`; los guarda el microservicio de soporte en `db_soporte`.
- La URL del Gateway se configura con `TRAMIGO_API_URL`; fuera de Docker usa por defecto `http://localhost:8090`, y en Compose se define `http://api-gateway:8090`.
- El `pom.xml` de `tramigo-java` apunta a Java 21 para ser compatible con el JDK local usado durante desarrollo. La imagen Docker puede compilarlo con JDK 25 usando `--release 21`.
- Esta es una primera integración; el resto de pantallas todavía requiere auditoría para dejar de acceder directamente a repositorios o bases de datos del monolito cuando corresponda.
