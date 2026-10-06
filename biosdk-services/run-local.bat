@echo off
setlocal EnableExtensions EnableDelayedExpansion
REM Service runner (no Docker). Linux / macOS / Git Bash: use run-local.sh
REM
REM   run-local.bat init | test | run | all

set "MODULE_DIR=%~dp0"
if "%MODULE_DIR:~-1%"=="\" set "MODULE_DIR=%MODULE_DIR:~0,-1%"
set "MODULE=biosdk-services"
set "JAR_VERSION=1.4.1-SNAPSHOT"
set "IMPL=io.mosip.mock.sdk.impl.SampleSDKV2"
set "PORT=9099"
set "CTX=/biosdk-service"
set "MOCK_SDK_GAV=io.mosip.mock.sdk:mock-sdk:1.4.1-SNAPSHOT:jar:jar-with-dependencies"
set "MOCK_SDK_JAR=mock-sdk-1.4.1-SNAPSHOT-jar-with-dependencies.jar"
set "LOCAL_DIR=%MODULE_DIR%\.local"

set "CMD=%~1"
if "%CMD%"=="" goto :usage
if /I "%CMD%"=="-h" goto :usage
if /I "%CMD%"=="--help" goto :usage
if /I "%CMD%"=="help" goto :usage
if /I "%CMD%"=="init" goto :init
if /I "%CMD%"=="test" goto :test
if /I "%CMD%"=="run" goto :run
if /I "%CMD%"=="all" goto :all

echo error: unknown command '%CMD%'
goto :usage

:usage
echo Local biosdk-services ^(REST — mock SDK on loader.path, :%PORT%%CTX%/^)
echo.
echo   run-local.bat init     package this module ^(skip tests^) and download mock SDK
echo   run-local.bat test     Maven unit tests
echo   run-local.bat run      start the jar with mock SDK ^(after init^)
echo   run-local.bat all      init + test
echo.
echo Endpoints:
call :print_endpoints
exit /b 1

:print_endpoints
echo     listen:     http://localhost:%PORT%%CTX%/
echo     health:     http://localhost:%PORT%%CTX%/
echo     actuator:   http://localhost:%PORT%%CTX%/actuator/health
echo     info:       http://localhost:%PORT%%CTX%/actuator/info
echo     env:        http://localhost:%PORT%%CTX%/actuator/env
echo     mappings:   http://localhost:%PORT%%CTX%/actuator/mappings
echo     swagger:    http://localhost:%PORT%%CTX%/swagger-ui.html
echo     swagger ui: http://localhost:%PORT%%CTX%/swagger-ui/index.html
echo     api-docs:   http://localhost:%PORT%%CTX%/v3/api-docs
echo     POST:       http://localhost:%PORT%%CTX%/init
echo                 http://localhost:%PORT%%CTX%/match
echo                 http://localhost:%PORT%%CTX%/check-quality
echo                 http://localhost:%PORT%%CTX%/extract-template
echo                 http://localhost:%PORT%%CTX%/segment
echo                 http://localhost:%PORT%%CTX%/convert-format
echo     impl:       %IMPL%
echo     profile:    local  ^(config server off^)
exit /b 0

:check_prereqs
where java >nul 2>&1
if errorlevel 1 (
  echo error: java is required on PATH
  exit /b 1
)
where mvn >nul 2>&1
if errorlevel 1 (
  echo error: mvn is required on PATH
  exit /b 1
)
exit /b 0

:find_mock_sdk
if defined BIOSDK_MOCK_SDK if exist "%BIOSDK_MOCK_SDK%" (
  set "MOCK_SDK=%BIOSDK_MOCK_SDK%"
  exit /b 0
)
set "MOCK_SDK=%LOCAL_DIR%\%MOCK_SDK_JAR%"
set "SIBLING_SDK=%MODULE_DIR%\..\..\mosip-mock-services\mock-sdk\target\%MOCK_SDK_JAR%"
if exist "%SIBLING_SDK%" (
  if not exist "%LOCAL_DIR%" mkdir "%LOCAL_DIR%"
  set "NEED_COPY=1"
  if exist "%MOCK_SDK%" (
    for %%A in ("%SIBLING_SDK%") do set "SIB_SIZE=%%~zA"
    for %%A in ("%MOCK_SDK%") do set "LOC_SIZE=%%~zA"
    if "!SIB_SIZE!"=="!LOC_SIZE!" set "NEED_COPY=0"
  )
  if "!NEED_COPY!"=="1" copy /Y "%SIBLING_SDK%" "%MOCK_SDK%" >nul
  exit /b 0
)
if exist "%MOCK_SDK%" exit /b 0
if exist "%MODULE_DIR%\%MOCK_SDK_JAR%" (
  set "MOCK_SDK=%MODULE_DIR%\%MOCK_SDK_JAR%"
  exit /b 0
)
exit /b 1

:download_mock_sdk
call :find_mock_sdk
if not errorlevel 1 exit /b 0
if not exist "%LOCAL_DIR%" mkdir "%LOCAL_DIR%"
echo ==^> downloading mock-sdk 1.4.1-SNAPSHOT ^(jar-with-dependencies^)
pushd "%MODULE_DIR%"
call mvn -q org.apache.maven.plugins:maven-dependency-plugin:3.11.0:copy "-Dartifact=%MOCK_SDK_GAV%" "-DoutputDirectory=%LOCAL_DIR%" "-Dgpg.skip=true"
set "RC=%ERRORLEVEL%"
popd
if not "%RC%"=="0" (
  echo error: failed to download mock-sdk. Set BIOSDK_MOCK_SDK or check Maven snapshots.
  exit /b %RC%
)
call :find_mock_sdk
if errorlevel 1 (
  echo error: mock-sdk jar missing after download
  exit /b 1
)
echo     sdk: %MOCK_SDK%
exit /b 0

:find_service_jar
if exist "%MODULE_DIR%\target\%MODULE%-%JAR_VERSION%.jar" (
  set "SERVICE_JAR=%MODULE_DIR%\target\%MODULE%-%JAR_VERSION%.jar"
  exit /b 0
)
for %%F in ("%MODULE_DIR%\target\%MODULE%-*.jar") do (
  echo %%~nxF | findstr /I "sources javadoc original" >nul
  if errorlevel 1 if exist "%%~fF" (
    set "SERVICE_JAR=%%~fF"
    exit /b 0
  )
)
exit /b 1

:init
call :check_prereqs
if errorlevel 1 exit /b 1
echo ==^> packaging %MODULE% ^(skip tests^)
pushd "%MODULE_DIR%"
call mvn clean package -DskipTests "-Dgpg.skip=true" "-Dmaven.javadoc.skip=true"
set "RC=%ERRORLEVEL%"
popd
if not "%RC%"=="0" exit /b %RC%
call :download_mock_sdk
if errorlevel 1 exit /b 1
echo init complete
exit /b 0

:test
call :check_prereqs
if errorlevel 1 exit /b 1
echo ==^> maven unit tests
pushd "%MODULE_DIR%"
call mvn test "-Dgpg.skip=true" "-Dmaven.javadoc.skip=true"
set "RC=%ERRORLEVEL%"
popd
exit /b %RC%

:run
call :check_prereqs
if errorlevel 1 exit /b 1
call :find_service_jar
if errorlevel 1 (
  echo error: service jar not found. Run: run-local.bat init
  exit /b 1
)
call :download_mock_sdk
if errorlevel 1 exit /b 1
if not exist "%LOCAL_DIR%\logs" mkdir "%LOCAL_DIR%\logs"
echo ==^> starting %MODULE% on :%PORT%%CTX%/
echo     jar: %SERVICE_JAR%
echo     sdk: %MOCK_SDK%
echo     loader.path: %MOCK_SDK%
echo     log: %LOCAL_DIR%\logs\%MODULE%.log
call :print_endpoints
pushd "%MODULE_DIR%"
java -Dloader.path="%MOCK_SDK%" -Dbiosdk_bioapi_impl=%IMPL% -Dspring.cloud.config.enabled=false -Dspring.profiles.active=local --add-modules=ALL-SYSTEM --add-opens java.xml/jdk.xml.internal=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.lang.stream=ALL-UNNAMED --add-opens java.base/java.time=ALL-UNNAMED --add-opens java.base/java.time.LocalDate=ALL-UNNAMED --add-opens java.base/java.time.LocalDateTime=ALL-UNNAMED --add-opens java.base/java.time.LocalDateTime.date=ALL-UNNAMED -jar "%SERVICE_JAR%"
set "RC=%ERRORLEVEL%"
popd
exit /b %RC%

:all
echo ==^> all: init + test
call :init
if errorlevel 1 exit /b 1
call :test
exit /b %ERRORLEVEL%
