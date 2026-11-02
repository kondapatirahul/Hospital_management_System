@echo off
setlocal
cd /d "%~dp0"

echo ========================================================
echo Starting CareFlow Healthcare Management System
echo ========================================================

where java >nul 2>nul
if errorlevel 1 (
    echo ERROR: Java is not installed or not available on PATH.
    echo Install JDK 17 or later and reopen this terminal.
    pause
    exit /b 1
)

where mvn >nul 2>nul
if errorlevel 1 (
    echo ERROR: Maven is not installed or not available on PATH.
    echo Install Apache Maven 3.9+ and reopen this terminal.
    pause
    exit /b 1
)

if not defined DB_URL set "DB_URL=jdbc:mysql://localhost:3306/hospital_db?createDatabaseIfNotExist=true^&useSSL=false^&allowPublicKeyRetrieval=true^&serverTimezone=UTC"
if not defined DB_USERNAME set "DB_USERNAME=root"

if not defined DB_PASSWORD (
    echo Set DB_PASSWORD before running this file, for example:
    echo   set DB_PASSWORD=your_mysql_password
    echo.
    set /p "DB_PASSWORD=MySQL password for %DB_USERNAME%: "
)

echo Java:
java -version
echo.
echo Database URL: %DB_URL%
echo Database user: %DB_USERNAME%
echo.

call mvn -f "%~dp0backend\pom.xml" clean spring-boot:run
set "APP_EXIT=%ERRORLEVEL%"
if not "%APP_EXIT%"=="0" (
    echo.
    echo CareFlow did not start. Review the Maven or Spring Boot error above.
)
pause
exit /b %APP_EXIT%
