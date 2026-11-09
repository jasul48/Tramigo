@echo off
call "%~dp0cargar_env.bat"
setlocal

title Tramigo - Iniciar todo

echo ===================================================
echo        INICIANDO ECOSISTEMA TRAMIGO (MICROSERVICIOS)
echo ===================================================
echo.
echo Requisitos: MySQL corriendo en localhost:3306.
echo Cada servicio se abre en su propia ventana.
echo.

:: 1) Eureka Server (debe arrancar primero)
start "Eureka Server (8761)" /D "%~dp0eureka-server" cmd /k "gradlew.bat bootRun"
echo [..] Esperando a que Eureka levante (25 s)...
timeout /t 25 /nobreak >nul

:: 2) Microservicios de dominio
start "usuarios-service (8081)" /D "%~dp0usuarios-service" cmd /k "mvnw.cmd spring-boot:run"
start "notificaciones-service (8084)" /D "%~dp0notificaciones-service" cmd /k "mvnw.cmd spring-boot:run"
start "tramites-service (8085)" /D "%~dp0tramites-service" cmd /k "mvnw.cmd spring-boot:run"
start "seguimiento-service (8086)" /D "%~dp0seguimiento-service" cmd /k "mvnw.cmd spring-boot:run"
start "documentos-service (8082)" /D "%~dp0documentos-service" cmd /k "mvnw.cmd spring-boot:run"
start "soporte-service (8083)" /D "%~dp0soporte-service" cmd /k "mvnw.cmd spring-boot:run"

:: 3) API Gateway (al final, cuando ya hay servicios registrados)
echo [..] Esperando a que los servicios se registren (20 s)...
timeout /t 20 /nobreak >nul
start "api-gateway (8090)" /D "%~dp0api-gateway" cmd /k "mvnw.cmd spring-boot:run"

echo.
echo ===================================================
echo  Eureka (dashboard):  http://localhost:8761
echo  API Gateway:         http://localhost:8090
echo ===================================================
pause
