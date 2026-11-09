@echo off
setlocal

set "DIR=%~dp0"
if "%DIR:~-1%"=="\" set "DIR=%DIR:~0,-1%"
set "WRAPPER_JAR=%DIR%\.mvn\wrapper\maven-wrapper.jar"

if not defined JAVA_HOME (
    where java >nul 2>nul
    if errorlevel 1 (
        if exist "C:\Program Files\Microsoft\jdk-17.0.20.1-hotspot\bin\java.exe" (
            set "JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.20.1-hotspot"
            set "PATH=C:\Program Files\Microsoft\jdk-17.0.20.1-hotspot\bin;%PATH%"
        ) else if exist "C:\Program Files\Eclipse Adoptium\jdk-17*\bin\java.exe" (
            for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk-17*") do (
                set "JAVA_HOME=%%D"
                set "PATH=%%D\bin;%PATH%"
            )
        )
    )
)

if defined JAVA_HOME (
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) else (
    set "JAVA_EXE=java"
)

"%JAVA_EXE%" "-Dmaven.multiModuleProjectDirectory=%DIR%" -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*

