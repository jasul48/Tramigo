@echo off
call "%~dp0cargar_env.bat"
title Tramigo - Notificaciones Service (Microservicio)
echo ===================================================
echo        INICIANDO NOTIFICACIONES SERVICE (SPRING BOOT)
echo ===================================================
echo.

:: Liberar puerto 8084 si habia quedado ocupado
powershell -NoProfile -Command "$p = (Get-NetTCPConnection -LocalPort 8084 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -First 1); if ($p) { Stop-Process -Id $p -Force -ErrorAction SilentlyContinue }" >nul 2>&1

cd /d "%~dp0notificaciones-service"

echo [OK] Puerto 8084 verificado y disponible.
echo [..] Compilando e iniciando microservicio Spring Boot...
echo.
echo ===================================================
echo  Servicio listo en: http://localhost:8084
echo  API REST:   http://localhost:8084/api/notificaciones
echo ===================================================
echo.

call .\mvnw.cmd spring-boot:run
pause
