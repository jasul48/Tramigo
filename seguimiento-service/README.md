# seguimiento-service

Microservicio independiente de seguimiento de solicitudes de Tramigo.

- Puerto local: `8086`
- Eureka: `seguimiento-service`
- Base de datos: `db_seguimiento`
- Ruta pública vía Gateway: `http://localhost:8090/api/seguimiento`

## Endpoints

- `POST /api/seguimiento/solicitudes` – registra una solicitud.
- `GET /api/seguimiento/solicitudes` – lista solicitudes.
- `GET /api/seguimiento/solicitudes?usuarioId=1` – filtra por usuario.
- `GET /api/seguimiento/solicitudes/{id}` – consulta detalle y estado.
- `PUT /api/seguimiento/solicitudes/{id}/estado` – actualiza el estado y agrega un evento al historial.
- `GET /api/seguimiento/solicitudes/{id}/historial` – consulta el historial.

### Crear solicitud

```json
{
  "usuarioId": 1,
  "tramiteId": 3,
  "titulo": "Renovación de INE",
  "descripcion": "Solicitud iniciada desde Tramigo"
}
```

### Actualizar estado

```json
{
  "estado": "EN_REVISION",
  "comentario": "Se revisaron los documentos entregados"
}
```

Estados permitidos: `RECIBIDA`, `EN_REVISION`, `EN_PROCESO`, `PENDIENTE_DOCUMENTOS`, `APROBADA`, `RECHAZADA`, `FINALIZADA`, `CANCELADA`.

El servicio crea automáticamente el primer evento del historial al registrar la solicitud. Cada actualización de estado agrega otro evento con fecha y comentario.

> Nota: `usuarioId` y `tramiteId` se almacenan como referencias numéricas. Este servicio todavía no valida remotamente su existencia en los microservicios de usuarios y trámites.
