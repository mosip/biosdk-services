# BioSDK Service

[![Maven Package upon a push](https://github.com/mosip/biosdk-services/actions/workflows/push-trigger.yml/badge.svg?branch=develop)](https://github.com/mosip/biosdk-services/actions/workflows/push-trigger.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?branch=develop&project=mosip_biosdk-services&metric=alert_status)](https://sonarcloud.io/dashboard?branch=develop&id=mosip_biosdk-services)

## Overview

**BioSDK Service** is the MOSIP REST wrapper around [`IBioApiV2`](https://github.com/mosip/bio-utils/blob/master/kernel-biometrics-api/src/main/java/io/mosip/kernel/biometrics/spi/IBioApiV2.java). It does **not** implement match / extract / segment / quality / convert itself. At startup it loads a vendor (or mock) SDK class named by `biosdk_bioapi_impl` from `-Dloader.path`, then exposes those operations as `POST` endpoints under `/biosdk-service/`.

Parent: [`../README.md`](../README.md)

Functional spec: [Biometric SDK](https://docs.mosip.io/1.2.0/biometrics/biometric-sdk). Request/response samples: [`docs/spec_1_0.md`](docs/spec_1_0.md).

`convertFormat` on this service calls `IBioApiV2.convertFormatV2`. Do not change `IBioApiV2` method signatures or the JSON envelope (`request` Base64 payload, top-level `errors`).

**Contents:** [Overview](#overview) · [At a glance](#at-a-glance) · [Features](#features) · [Architecture](#architecture) · [REST operations](#rest-operations) · [Usage](#usage) · [Configurations](#configurations) · [Tests](#tests) · [Deployment](#deployment) · [License](#license)

---

## At a glance

| Item | Value |
|------|--------|
| Artifact | `io.mosip.biosdk:biosdk-services` `1.4.1-SNAPSHOT` |
| JDK / Boot | **21** / Spring Boot **4.1.1** (Framework 7) |
| MOSIP | `kernel-core` **1.4.1-SNAPSHOT** · `kernel-biometrics-api` **1.4.0-SNAPSHOT** · no `kernel-bom` |
| Main type | `io.mosip.biosdk.services.SdkApplication` |
| Controller | `io.mosip.biosdk.services.controller.MainController` |
| SPI | `IBioApiV2` loaded by `BioSdkLibConfig` from `biosdk_bioapi_impl` |
| Spec impl | `BioSdkServiceProviderImpl_V_1_0` (`version` **`"1.0"`**) |
| JSON | Jackson **2.x** via `spring-boot-jackson2` (not Jackson 3) |
| Packaged as | Spring Boot **ZIP** layout JAR (`PropertiesLauncher` + `loader.path`) |
| Listen | **`:9099`** `/biosdk-service/` |
| Tests | JUnit (mocked `IBioApiV2` — no live SDK) |

This module is a **Spring Boot process**. Local work uses Maven + `run-local` — not Docker.

---

## Features

- REST facade over any `IBioApiV2` JAR on `loader.path`
- Default mock SDK: `io.mosip.mock.sdk.impl.SampleSDKV2`
- 1:N match, template extract, segment, quality check, convert (`convertFormatV2`)
- JSON envelope: typed DTO → Base64 `request` → SDK → top-level `errors`
- Versioned provider factory (`BioSdkServiceFactory` selects by `RequestDto.version`)
- Local profile: no config server (`spring.cloud.config.enabled=false`)
- Actuator health + Swagger UI

This service is **not** a biometric SDK. The vendor/mock JAR does the biometrics.

---

## Architecture

```text
host (biosdk-client / Postman)
        │
        │  POST /biosdk-service/init|/match|/extract-template
        │       /segment|/check-quality|/convert-format
        ▼
MainController  ──version──►  BioSdkServiceFactory
                                      │
                                      ▼
                         BioSdkServiceProviderImpl_V_1_0
                                      │
                                      ▼
                    IBioApiV2  (vendor / mock SDK on loader.path)
```

`BioSdkLibConfig` reflects `biosdk_bioapi_impl` after the Spring Boot loader path has the SDK JAR. Layout **ZIP** is required for `-Dloader.path`.

---

## REST operations

Context path `/biosdk-service`. All POST bodies are `RequestDto`.

| Path | Method | Operation |
|------|--------|-----------|
| `/` | `GET` | Health text (`Service is running...`) |
| `/init` | `POST` | SDK `init` |
| `/check-quality` | `POST` | Quality assessment |
| `/match` | `POST` | 1:N match (sample + gallery) |
| `/extract-template` | `POST` | Feature extraction |
| `/segment` | `POST` | Segmentation |
| `/convert-format` | `POST` | Calls `convertFormatV2` |

Envelope:

```json
{
  "version": "1.0",
  "request": "<Base64 of the operation DTO JSON>"
}
```

Response JSON always includes top-level `errors` (empty list on success). HTTP status is **200** even when `errors` is populated.

Health: `http://localhost:9099/biosdk-service/`  
Swagger: `http://localhost:9099/biosdk-service/swagger-ui.html`

---

## Usage

This module **is** a Spring Boot process. Package and test with Maven; start it with **`run-local`**. Run scripts from **`biosdk-services/`**.

| You want | Windows cmd | Linux / macOS / Git Bash |
|----------|-------------|--------------------------|
| Help | `run-local.bat` | `./run-local.sh` |
| Package the JAR | `run-local.bat init` | `./run-local.sh init` |
| Unit tests | `run-local.bat test` | `./run-local.sh test` |
| Start (mock SDK) | `run-local.bat run` | `./run-local.sh run` |
| Package + tests | `run-local.bat all` | `./run-local.sh all` |

`bash` on many Windows PCs is **WSL** and does not inherit Windows `JAVA_HOME`. Prefer **cmd** (or Git Bash). PowerShell: `.\run-local.bat test` and quote `"-Dgpg.skip=true"`.

### Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| JDK | **21** | `java -version` |
| Maven | **3.9+** | PowerShell: quote `"-Dgpg.skip=true"` |
| Mock SDK JAR | `*-jar-with-dependencies.jar` | Needed for `run` only, not `test`. Fat mock-sdk shades Boot 3; `run-local` rebuilds `.local/mock-sdk-loader.jar` with mock SDK + OpenCV only (no Spring/Tomcat/Logback). |

No Docker. No PostgreSQL. No config server. File logs: `biosdk-services/.local/logs/` (gitignored).

### Windows (cmd)

```bat
cd biosdk-services
run-local.bat init
run-local.bat test
run-local.bat run
```

### Linux / macOS / Git Bash

```bash
cd biosdk-services
chmod +x run-local.sh
./run-local.sh init
./run-local.sh test
./run-local.sh run
```

### Module runner (`run-local.sh` / `run-local.bat`)

| Command | What it does |
|---------|----------------|
| `init` | `mvn clean package` skip tests, then download mock SDK into `.local/` |
| `test` | Unit suite (mocked SDK) |
| `run` | Start the ZIP JAR with mock SDK, `spring.profiles.active=local`, config server **off** |
| `all` | `init` + `test` |

`init` / `run` download `io.mosip.mock.sdk:mock-sdk:1.4.0-SNAPSHOT` (`jar-with-dependencies`) into `biosdk-services/.local/` when it is missing. Override with `BIOSDK_MOCK_SDK`. If that JAR contains Spring Boot or Logback classes, `run` rebuilds `.local/mock-sdk-loader.jar` with only the mock `IBioApiV2` implementation, OpenCV, and image-format classes (no Spring/Tomcat/Logback). A vendor SDK that does not shade Spring can be used as-is.

### Manual Maven

```text
mvn clean install "-Dgpg.skip=true"
mvn test
mvn test "-Dtest=MainControllerTest"
```

PowerShell: always quote `"-D..."`.

Start without the runner (after `init`):

```bat
java -Dloader.path=<biosdk-services/.local/mock-sdk-loader.jar or vendor SDK JAR> -Dbiosdk_bioapi_impl=io.mosip.mock.sdk.impl.SampleSDKV2 -Dspring.cloud.config.enabled=false -Dspring.profiles.active=local --add-modules=ALL-SYSTEM --add-opens java.xml/jdk.xml.internal=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.lang.stream=ALL-UNNAMED --add-opens java.base/java.time=ALL-UNNAMED --add-opens java.base/java.time.LocalDate=ALL-UNNAMED --add-opens java.base/java.time.LocalDateTime=ALL-UNNAMED --add-opens java.base/java.time.LocalDateTime.date=ALL-UNNAMED -jar target/biosdk-services-1.4.1-SNAPSHOT.jar
```

### IDE

Open the `biosdk-services` Maven module. Run JUnit (`MainControllerTest`, `BioSdkServiceProviderImpl_V_1_0Test`, …) or `SdkApplication` with the same `-Dloader.path` / `-Dbiosdk_bioapi_impl` / `-Dspring.cloud.config.enabled=false` / `-Dspring.profiles.active=local` VM options as `run`.

### Verify

```text
http://localhost:9099/biosdk-service/
```

Expected:

```text
Service is running... ...
```

### Do not

- Do not start a config server for laptop `run-local`.
- Do not drop layout ZIP — `loader.path` needs `PropertiesLauncher`.
- Do not point `biosdk_bioapi_impl` at a class that is not on `loader.path`.

### Troubleshooting

| Symptom | What to do |
|---------|------------|
| Config server connection refused | Use `run-local.bat run` (sets `spring.cloud.config.enabled=false`) or `-Dspring.profiles.active=local` |
| `ClassNotFoundException` for `SampleSDKV2` | `run-local.bat init` (downloads mock SDK) or set `BIOSDK_MOCK_SDK` |
| `service jar not found` | `run-local.bat init` first |
| Port **9099** in use | Stop the other process, then `run` again |

---

## Configurations

### SDK loading (required at process start)

| Property / flag | Role |
|-----------------|------|
| `-Dloader.path` | Directory or JAR that contains the `IBioApiV2` implementation |
| `biosdk_bioapi_impl` | Fully qualified class name (default mock: `io.mosip.mock.sdk.impl.SampleSDKV2`) |

`BioSdkLibConfig` instantiates that class with a no-arg constructor.

### Local profile (`application-local.properties`)

`run-local … run` sets `-Dspring.profiles.active=local` and `-Dspring.cloud.config.enabled=false`.

```properties
spring.cloud.config.enabled=false
biosdk_bioapi_impl=io.mosip.mock.sdk.impl.SampleSDKV2
```

### Remote config (cluster)

Without `local`, bootstrap talks to a config server. Remote files live in [mosip-config](https://github.com/mosip/mosip-config/tree/master):

1. [application-default.properties](https://github.com/mosip/mosip-config/blob/master/application-default.properties)
2. [biosdk-service-default.properties](https://github.com/mosip/mosip-config/blob/master/biosdk-service-default.properties)

Do **not** require that stack for `run-local`.

---

## Tests

| Class | When it runs |
|-------|----------------|
| `MainControllerTest`, `BioSdkServiceProviderImpl_V_1_0Test`, `BioSdkServiceFactoryTest`, `BioSdkLibConfigTest`, `SecurityConfigTest`, `UtilsTest`, `BioSDKExceptionTest` | Default `mvn test` / `run-local.bat test` |

Unit tests mock `IBioApiV2`. They do **not** start Tomcat or need a mock-SDK JAR.

---

## Deployment

Local: `run-local.bat run` / `./run-local.sh run`. **No Docker** for laptop work.

Cluster: Docker image `biosdk-server` (module `Dockerfile` downloads the SDK zip into `loader.path`). Helm: [`../helm/biosdk-service/`](../helm/biosdk-service/). Install scripts: [`../deploy/`](../deploy/). Infra: [mosip-infra](https://github.com/mosip/mosip-infra).

**Note:** set `biosdk_bioapi_impl` and the SDK zip URL on containers that run this service.

---

## Documentation

| Doc | Content |
|-----|---------|
| [Biometric SDK](https://docs.mosip.io/1.2.0/biometrics/biometric-sdk) | Product / spec |
| [`docs/spec_1_0.md`](docs/spec_1_0.md) | HTTP request/response samples |
| [biosdk-client](https://github.com/mosip/biosdk-client) | HTTP `IBioApiV2` caller |

---

## Contribution & Community

• To learn how you can contribute code to this application, [click here](https://docs.mosip.io/1.2.0/community/code-contributions).

• If you have questions or encounter issues, visit the [MOSIP Community](https://community.mosip.io/) for support.

• For any GitHub issues: [Report here](https://github.com/mosip/biosdk-services/issues)

---

## License

This project is licensed under the [Mozilla Public License 2.0](../LICENSE).

Third-party components are listed with the JAR versions resolved from this build in [THIRD-PARTY-NOTICES.txt](../THIRD-PARTY-NOTICES.txt). Approved licenses (compatible with MPL 2.0 **and** allowed with MOSIP) are in [licenses/](../licenses/). Two Boot/kernel transitives (`aspectjweaver`, `jakarta.annotation-api`) are called out there because their licenses are not on the MOSIP Yes list.
