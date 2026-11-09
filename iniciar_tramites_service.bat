@echo off
call "%~dp0cargar_env.bat"
title Tramigo - Tramites Service (Microservicio)
echo ===================================================
echo        INICIANDO TRAMITES SERVICE (SPRING BOOT)
echo ===================================================
echo.

:: Liberar puerto 8085 si habia quedado ocupado
powershell -NoProfile -Command "$p = (Get-NetTCPConnection -LocalPort 8085 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -First 1); if ($p) { Stop-Process -Id $p -Force -ErrorAction SilentlyContinue }" >nul 2>&1

cd /d "%~dp0tramites-service"

echo [OK] Puerto 8085 verificado y disponible.
echo [..] Compilando e iniciando microservicio Spring Boot...
echo.
echo ===================================================
echo  Servicio listo en: http://localhost:8085
echo  API REST:   http://localhost:8085/api/tramites
echo ===================================================
echo.

call .\mvnw.cmd spring-boot:run
pause
