@echo off
call "%~dp0cargar_env.bat"
title Tramigo - Documentos Service (Microservicio)
echo ===================================================
echo        INICIANDO DOCUMENTOS SERVICE (SPRING BOOT)
echo ===================================================
echo.

:: Liberar puerto 8082 si habia quedado ocupado
powershell -NoProfile -Command "$p = (Get-NetTCPConnection -LocalPort 8082 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -First 1); if ($p) { Stop-Process -Id $p -Force -ErrorAction SilentlyContinue }" >nul 2>&1

cd /d "%~dp0documentos-service"

echo [OK] Puerto 8082 verificado y disponible.
echo [..] Compilando e iniciando microservicio Spring Boot...
echo.
echo ===================================================
echo  Servicio listo en: http://localhost:8082
echo  API REST:   http://localhost:8082/api/documentos/usuario/{usuarioId}
echo ===================================================
echo.

call .\mvnw.cmd spring-boot:run
pause
