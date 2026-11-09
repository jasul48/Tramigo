-- ============================================================
-- Migracion de datos del monolito (logintramigo) a los nuevos microservicios
--
-- PASOS:
--  1) Arranca UNA VEZ documentos-service y soporte-service
--     (Hibernate crea las tablas en db_documentos y db_soporte).
--  2) Ejecuta este script en MySQL.
--  3) Copia los archivos fisicos:  tramigo-java/uploads/documentos
--                              ->  documentos-service/uploads/documentos
--
-- Verifica antes que las columnas de logintramigo coincidan con las de abajo.
-- ============================================================

INSERT INTO db_documentos.Documentos
    (documento_id, usuario_id, nombre_personalizado, nombre_archivo, tipo_archivo, fecha_subida)
SELECT documento_id, usuario_id, nombre_personalizado, nombre_archivo, tipo_archivo, fecha_subida
FROM logintramigo.Documentos;

INSERT INTO db_soporte.Soporte
    (ticket_id, nombre, correo, asunto, mensaje, estado, fecha_creacion)
SELECT ticket_id, nombre, correo, asunto, mensaje, estado, fecha_creacion
FROM logintramigo.Soporte;
