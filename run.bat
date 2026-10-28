@echo off
setlocal
echo ========================================================
echo Starting CareFlow Healthcare Management System (M1)
echo ========================================================

:: Check for Java 17+
if defined JAVA_HOME (
    set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
) else (
    set "JAVA_CMD=java"
)

:: Set JDK 25 if available in default directory
if exist "C:\Program Files\Java\jdk-25.0.2\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-25.0.2"
    set "PATH=C:\Program Files\Java\jdk-25.0.2\bin;%PATH%"
)

:: Find Maven
if exist "C:\Users\dell\Downloads\apache-maven-3.10.0-bin\apache-maven-3.10.0\bin\mvn.cmd" (
    set "MVN_CMD=C:\Users\dell\Downloads\apache-maven-3.10.0-bin\apache-maven-3.10.0\bin\mvn.cmd"
) else (
    set "MVN_CMD=mvn"
)

echo Using JAVA_HOME: %JAVA_HOME%
echo Using Maven: %MVN_CMD%
echo.

call "%MVN_CMD%" -f "%~dp0backend\pom.xml" spring-boot:run
pause
