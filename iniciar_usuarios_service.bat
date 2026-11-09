@echo off
call "%~dp0cargar_env.bat"
title Tramigo - Usuarios Service (Microservicio)
echo ===================================================
echo        INICIANDO USUARIOS SERVICE (SPRING BOOT)
echo ===================================================
echo.

:: Liberar puerto 8081 si habia quedado ocupado
powershell -NoProfile -Command "$p = (Get-NetTCPConnection -LocalPort 8081 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -First 1); if ($p) { Stop-Process -Id $p -Force -ErrorAction SilentlyContinue }" >nul 2>&1

cd /d "%~dp0usuarios-service"

echo [OK] Puerto 8081 verificado y disponible.
echo [..] Compilando e iniciando microservicio Spring Boot...
echo.
echo ===================================================
echo  Servicio Web listo en: http://localhost:8081
echo  Login:                 http://localhost:8081/login
echo  Registro:              http://localhost:8081/registro
echo  CRUD Personas:         http://localhost:8081/crud
echo  API REST:              http://localhost:8081/api/usuarios
echo                         http://localhost:8081/api/personas
echo ===================================================
echo.

call .\mvnw.cmd spring-boot:run
pause
