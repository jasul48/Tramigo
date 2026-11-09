@echo off
call "%~dp0cargar_env.bat"
title Tramigo - API Gateway (Microservicio)
echo ===================================================
echo        INICIANDO API GATEWAY (SPRING BOOT)
echo ===================================================
echo.

:: Liberar puerto 8090 si habia quedado ocupado
powershell -NoProfile -Command "$p = (Get-NetTCPConnection -LocalPort 8090 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -First 1); if ($p) { Stop-Process -Id $p -Force -ErrorAction SilentlyContinue }" >nul 2>&1

cd /d "%~dp0api-gateway"

echo [OK] Puerto 8090 verificado y disponible.
echo [..] Compilando e iniciando microservicio Spring Boot...
echo.
echo ===================================================
echo  Servicio listo en: http://localhost:8090
echo  Punto unico de entrada: http://localhost:8090/api/...
echo ===================================================
echo.

call .\mvnw.cmd spring-boot:run
pause
