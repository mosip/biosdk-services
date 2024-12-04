# Bio SDK services

## Overview

This service offers a mock implementation of the Bio-SDK REST Service. By default, it initializes the [Mock BIO SDK](https://github.com/mosip/mosip-mock-services/tree/master/mock-sdk) during startup and exposes endpoints for performing 1:N matching, segmentation, and extraction, adhering to the [IBioAPI](https://github.com/mosip/commons/blob/master/kernel/kernel-biometrics-api/src/main/java/io/mosip/kernel/biometrics/spi/IBioApi.java) specification. 

Additionally, the service is configurable to load an alternative JAR implementing `IBioAPIV2`, provided all necessary dependencies are satisfied. This flexibility allows users to integrate customized implementations seamlessly. 

## Table of Contents

- [Prerequisites](#prerequisites)
- [Setting Up Locally](#setting-up-locally)
- [Running the Application](#running-the-application)
- [Configurations](#configurations)
- [APIs Provided](#apis-provided)
- [API Documentation](#api-documentation)
- [License](#license)

## Prerequisites

Ensure you have the following installed before proceeding:

1. **Java**: Version 21.0.3
2. **Maven**: For building the project 3.9.6
3. **Git**: To clone the repository
4. **Postman (optional)**: For testing the APIs

---

## Setting Up Locally

### Follow these steps to set up the project on your local environment:

1. **Clone the repository**

Clone the repository from GitHub to your local machine:

```bash
git clone https://github.com/mosip/biosdk-services.git
cd biosdk-services
```

2. **Build the project**

Use Maven to build the project and resolve dependencies.

```bash
	mvn clean install
```
   

3. **Start the application**

Run the application using the following command:

```java
java -Dloader.path=<biosdk jar provided by third-party vendors> -Dbiosdk_bioapi_impl=<classpath of class that implements IBioApi interface> --add-modules=ALL-SYSTEM --add-opens java.xml/jdk.xml.internal=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.lang.stream=ALL-UNNAMED --add-opens java.base/java.time=ALL-UNNAMED --add-opens java.base/java.time.LocalDate=ALL-UNNAMED --add-opens java.base/java.time.LocalDateTime=ALL-UNNAMED --add-opens java.base/java.time.LocalDateTime.date=ALL-UNNAMED  -jar biosdk-services-<version>.jar
```

For example:

```java
java -Dloader.path=mock-sdk-1.3.0-SNAPSHOT-jar-with-dependencies.jar -Dbiosdk_bioapi_impl=io.mosip.mock.sdk.impl.SampleSDKV2 --add-modules=ALL-SYSTEM --add-opens java.xml/jdk.xml.internal=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.lang.stream=ALL-UNNAMED --add-opens java.base/java.time=ALL-UNNAMED --add-opens java.base/java.time.LocalDate=ALL-UNNAMED --add-opens java.base/java.time.LocalDateTime=ALL-UNNAMED --add-opens java.base/java.time.LocalDateTime.date=ALL-UNNAMED -jar biosdk-services-1.3.0-SNAPSHOT.jar
```

4. **Verify the setup**

Once the application is running, confirm that it is operational by accessing the health check endpoint:

For your host:

```text
   http://{host}:9099/biosdk-service
```

In case of localhost:

```text
   http://localhost:9099/biosdk-service
```

You will see the below response:

```text
   Service is running... Fri Jan 29 08:49:28 UTC 2021
```
---


## Running the Application

### Using Docker (Optional)

You can run the application with Docker for easier deployment and environment isolation.

1. **Build the Docker image**

Before building the image, set the following environment variables:

* biosdk_zip_url - The URL pointing to the third-party BioSDK library ZIP file.
* biosdk_bioapi_impl - The fully qualified class name implementing the IBioApi interface.

Run the below command to build the Docker image:

```bash
   docker build -t mosip/biosdk-services .
```

2. **Run the Docker container**

Run the below command:

```bash
   docker run -d -p 8080:8080 mosip/biosdk-services
```

3. **Verify the container**

Ensure the application is running correctly by following these steps:

* **Check running containers:**

Use the docker ps command to verify that the container is up and running:

```bash
   docker ps
```
* **Test the health endpoint:**

Use curl to check the application's health:

```bash
   curl http://localhost:9099/health
```

* **Expected Response:**

If the setup is successful, you will see a response similar to below:

```text
	Service is running... Fri Jan 29 08:49:28 UTC 2021
```
---

## Configurations

Configurations can be customized in the `application.properties` file located in the `src/main/resources` directory. Common properties include:

| Property Name                          | Description                                      | Default Value                        |
|----------------------------------------|---------------------------------------------------|--------------------------------------|
| `sdk_check_iso_timestamp_format`       | if we want to check ISO Dateformat                | `true`                                         |
| `biosdk_class`                         | class that implements IBioApi interface methods    | `io.mosip.mock.sdk.impl.SampleSDKV2` |
| `biosdk_bioapi_impl`                   | class that implements IBioApi interface methods               | `io.mosip.mock.sdk.impl.SampleSDKV2` |

---

## APIs Provided

BioSDK-Services provides the following key endpoints:

| Endpoint                | Method | Description                     |
|-------------------------|--------|---------------------------------|
| `/biosdk-service/init`               | POST    | Can be used to init the SDK    |
| `/biosdk-service/match`      | POST   | Validates biometric matching with probe and gallery       |
| `/biosdk-service/segment`        | POST   | Segmentation of biometric records |
| `/biosdk-service/extract-template` | POST    | Extract the template for the given biometric record      |
| `/biosdk-service/convert-format` | POST    | Convert biometric record ISO to JPEG/PNG using  [here](https://github.com/mosip/converters/tree/develop).       |
| `/biosdk-service/check-quality` | POST    | Check the quality for the a given biometric record      |



## Documentation

Please refer to the [Documentation](https://github.com/mosip/documentation/blob/1.2.0/docs/biometric-sdk.md) to know more about Bio SDK 

---

## API Documentation

Swagger UI to explore the exposed APIs:

For your host:

```text
http://{host}:9099/biosdk-service/swagger-ui.html
```

In case of localhost:

```
http://localhost:9099/biosdk-service/swagger-ui.html
```
---

## License

This project is licensed under the terms of [Mozilla Public License 2.0](https://github.com/mosip/admin-services/blob/develop/LICENSE).

---
