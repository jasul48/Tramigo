@echo off
call "%~dp0cargar_env.bat"
title Tramigo Web - Java Spring Boot
echo ===================================================
echo           INICIANDO TRAMIGO WEB (JAVA)
echo ===================================================
echo.

:: Liberar puerto 8080 si habia quedado ocupado
powershell -NoProfile -Command "$p = (Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -First 1); if ($p) { Stop-Process -Id $p -Force -ErrorAction SilentlyContinue }" >nul 2>&1

cd /d "%~dp0tramigo-java"

echo [OK] Puerto 8080 verificado y disponible.
echo [..] Compilando e iniciando servidor Spring Boot...
echo.
echo ===================================================
echo  La aplicacion estara lista en: http://localhost:8080
echo  Para detener el servidor, cierra esta ventana (Ctrl+C).
echo ===================================================
echo.

call .\mvnw.cmd spring-boot:run
echo http://localhost:8080
pause
