@echo off
call "%~dp0cargar_env.bat"
cd /d "%~dp0seguimiento-service"
call mvnw.cmd spring-boot:run
pause
