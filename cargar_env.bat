@echo off
if not exist "%~dp0.env" (
  echo [AVISO] No existe .env. Copia .env.example a .env y configura DB_PASSWORD.
  exit /b 0
)
for /f "usebackq eol=# tokens=1,* delims==" %%A in ("%~dp0.env") do (
  if not "%%A"=="" set "%%A=%%B"
)
