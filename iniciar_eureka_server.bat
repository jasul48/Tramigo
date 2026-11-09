@echo off
call "%~dp0cargar_env.bat"
title Tramigo - Eureka Discovery Server
echo ===================================================
echo        INICIANDO EUREKA SERVER (SPRING CLOUD)
echo ===================================================
echo.

:: Liberar puerto 8761 si habia quedado ocupado
powershell -NoProfile -Command "$p = (Get-NetTCPConnection -LocalPort 8761 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -First 1); if ($p) { Stop-Process -Id $p -Force -ErrorAction SilentlyContinue }" >nul 2>&1

cd /d "%~dp0eureka-server"

echo [OK] Puerto 8761 verificado y disponible.
echo [..] Compilando e iniciando Eureka Discovery Server...
echo.
echo ===================================================
echo  Eureka Dashboard listo en: http://localhost:8761
echo ===================================================
echo.

call .\gradlew.bat bootRun
pause
