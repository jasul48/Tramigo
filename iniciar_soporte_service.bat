@echo off
call "%~dp0cargar_env.bat"
title Tramigo - Soporte Service (Microservicio)
echo ===================================================
echo        INICIANDO SOPORTE SERVICE (SPRING BOOT)
echo ===================================================
echo.

:: Liberar puerto 8083 si habia quedado ocupado
powershell -NoProfile -Command "$p = (Get-NetTCPConnection -LocalPort 8083 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -First 1); if ($p) { Stop-Process -Id $p -Force -ErrorAction SilentlyContinue }" >nul 2>&1

cd /d "%~dp0soporte-service"

echo [OK] Puerto 8083 verificado y disponible.
echo [..] Compilando e iniciando microservicio Spring Boot...
echo.
echo ===================================================
echo  Servicio listo en: http://localhost:8083
echo  API REST:   http://localhost:8083/api/soporte
echo ===================================================
echo.

call .\mvnw.cmd spring-boot:run
pause
