# Tramigo: guía rápida de Docker

## Qué incluye

- MySQL 8.4 con bases separadas para los cinco servicios y `logintramigo`.
- Eureka, API Gateway y los cinco microservicios de negocio actuales.
- Volúmenes persistentes para MySQL y archivos subidos por usuarios/documentos.
- La web antigua `tramigo-java` queda como perfil opcional porque necesita que se importe/restaure el esquema y los datos históricos de `logintramigo`.

## Preparación en Windows

1. Instala y abre Docker Desktop.
2. Copia `.env.example` a `.env`.
3. Edita `.env` y define una contraseña de MySQL válida en `DB_PASSWORD`. Para envío real de correo, completa también `MAIL_USERNAME`, `MAIL_PASSWORD` y `SOPORTE_EMAIL`.
4. Desde la carpeta donde está `compose.yaml`, ejecuta:

```powershell
docker compose config
docker compose up --build -d
docker compose ps
docker compose logs -f eureka-server api-gateway
```

5. Comprueba Eureka en `http://localhost:8761` y el Gateway en `http://localhost:8090`.
6. Para detener los contenedores sin borrar datos: `docker compose down`. **No uses `docker compose down -v` si quieres conservar la base de datos.**

> Si ya tienes MySQL ejecutándose en Windows en el puerto 3306, puede haber conflicto con el puerto publicado por Compose. Detén el MySQL local mientras uses Compose o cambia el puerto publicado. Dentro de Docker, los servicios se conectan al host `mysql`, no a `localhost`.

## Activar la web original (opcional)

La web `tramigo-java` usa tablas históricas del monolito. La inicialización de Docker crea la base vacía `logintramigo`, pero no inventa ni restaura los datos originales. Primero importa el respaldo/esquema de la base original y luego ejecuta:

```powershell
docker compose --profile web up --build -d
```

La web estará en `http://localhost:8080`.

## Ejecutar fuera de Docker

Los archivos `iniciar_*.bat` ahora intentan cargar las variables de `.env`. Copia `.env.example` a `.env` y configura las credenciales correctas para tu MySQL local antes de ejecutar los scripts.

## Nota sobre el sexto microservicio

El código recibido contiene cinco microservicios de negocio: usuarios, documentos, soporte, notificaciones y trámites. El Gateway y Eureka son componentes de infraestructura y no se han contado como microservicios de negocio. No se agregó un sexto servicio inventado porque aún no se ha confirmado cuál exige la consigna. Una separación posible sería `requisitos-service`, pero debe validarse con el profesor o con el requisito original antes de implementarlo.

## Limitaciones conocidas para la entrega

- No se ha podido ejecutar un build ni `docker compose up` en el entorno de análisis porque no hay Docker ni Java 25 instalados. Los archivos deben probarse en la computadora de desarrollo.
- La web y las APIs todavía requieren integración funcional en sus pantallas.
- Configura credenciales mediante `.env`; no compartas ese archivo ni lo subas al repositorio. Las credenciales que estaban escritas directamente en los archivos del ZIP deben rotarse.
