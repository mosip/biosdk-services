# AGENTS.md

This file provides guidance to AI agents when working with code in this repository.
## Project Overview

`biosdk-services` is a Spring Boot REST service that wraps MOSIP's `IBioApiV2` biometric SDK interface and exposes it over HTTP. It is used by the MOSIP registration processor to perform biometric operations (match, segment, extract, quality check, format convert). The service loads a third-party SDK JAR at startup via `-Dloader.path` and `-Dbiosdk_bioapi_impl`, so the actual biometric logic lives outside this repo.

## Build & Run

All Maven commands must be run from `biosdk-services/` (the inner module directory), not the repo root.

```bash
# Build (skip GPG signing for local dev)
cd biosdk-services
mvn clean install -Dgpg.skip=true

# Run tests only
mvn test

# Run a single test class
mvn test -Dtest=MainControllerTest

# Run with mock SDK (after build)
java -Dloader.path=mock-sdk-1.4.0-SNAPSHOT-jar-with-dependencies.jar \
     -Dbiosdk_bioapi_impl=io.mosip.mock.sdk.impl.SampleSDKV2 \
     --add-modules=ALL-SYSTEM \
     --add-opens java.xml/jdk.xml.internal=ALL-UNNAMED \
     --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
     --add-opens java.base/java.lang.stream=ALL-UNNAMED \
     --add-opens java.base/java.time=ALL-UNNAMED \
     --add-opens java.base/java.time.LocalDate=ALL-UNNAMED \
     --add-opens java.base/java.time.LocalDateTime=ALL-UNNAMED \
     --add-opens java.base/java.time.LocalDateTime.date=ALL-UNNAMED \
     -jar target/biosdk-services-1.4.0-SNAPSHOT.jar
```

The `run_local.bat` script captures the full run command for Windows. Health check: `http://localhost:9099/biosdk-service/`. Swagger UI: `http://localhost:9099/biosdk-service/swagger-ui.html`.

## Key Configuration

- **`biosdk_bioapi_impl`** — fully qualified class name of the `IBioApiV2` implementation to load. Required at startup; must be on the `-Dloader.path`.
- **`spring.cloud.config.enabled`** — set to `false` (via `application-local.properties`) for local development without a config server.
- Config server integration: `bootstrap.properties` points at `http://localhost:51000/config`; remote config files live in [mosip-config](https://github.com/mosip/mosip-config/tree/master) (`application-default.properties`, `biosdk-service-default.properties`).
- Java 21 with `--enable-preview` is required. The compiler and surefire plugin both pass `--enable-preview`.

## Architecture

```
MainController               ← single REST controller, all /biosdk-service/* endpoints
    │
    └─ BioSdkServiceFactory  ← selects BioSdkServiceProvider by request.version
           │
           └─ BioSdkServiceProvider (SPI interface)
                  │
                  └─ BioSdkServiceProviderImpl_V_1_0  ← current only implementation (spec "1.0")
                         │
                         └─ IBioApiV2  ← loaded dynamically at startup by BioSdkLibConfig
```

**Request flow**: Every endpoint receives a `RequestDto` (wrapper with `version` + base64-encoded `request` payload). `MainController` asks the factory for the matching `BioSdkServiceProvider` by version string, then delegates. `BioSdkServiceProviderImpl_V_1_0` deserializes the inner request DTO, calls `IBioApiV2`, and returns the result wrapped in `ResponseDto`.

**SDK loading** (`BioSdkLibConfig`): At startup, `@PostConstruct` validates the class name from `biosdk_bioapi_impl`. The `IBioApiV2` bean is created lazily via reflection using that class name; it must exist on the Spring Boot loader path (`-Dloader.path`).

**Versioning**: The factory pattern in `BioSdkServiceFactory` is designed to support multiple spec versions. Adding a new version means implementing `BioSdkServiceProvider`, annotating it `@Component`, and setting `getSpecVersion()` accordingly — Spring will auto-inject it into the factory's list.

## API Endpoints

All endpoints are under `/biosdk-service/` (context path). All POST endpoints consume and produce `application/json`. The request body is always `RequestDto` with a `version` field and a base64-encoded `request` field.

| Path | Method | Operation |
|---|---|---|
| `/` | GET | Health check |
| `/init` | POST | SDK initialization |
| `/match` | POST | Biometric 1:N matching |
| `/check-quality` | POST | Quality assessment |
| `/extract-template` | POST | Feature extraction |
| `/segment` | POST | Biometric segmentation |
| `/convert-format` | POST | ISO to JPEG/PNG conversion |

## Sonar / Code Coverage

Sonar exclusions are configured for `dto/`, `config/`, `constants/`, and `Status/` packages. Run with the `sonar` Maven profile: `mvn verify -Psonar`.