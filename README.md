# Bio-SDK Service
[![Maven Package upon a push](https://github.com/mosip/biosdk-services/actions/workflows/push-trigger.yml/badge.svg?branch=develop)](https://github.com/mosip/biosdk-services/actions/workflows/push-trigger.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?branch=develop&project=mosip_biosdk-services&id=mosip_biosdk-services&metric=alert_status)](https://sonarcloud.io/dashboard?branch=develop&id=mosip_biosdk-services)

## Overview
This is reference service and provides a mock implementation of Bio-SDK REST Service. By default loads [Mock BIO SDK](https://github.com/mosip/mosip-mock-services/tree/master/mock-sdk) internally on the startup and exposes the endpoints to perform 1:1 match, extraction as per the [IBioAPIV2](https://github.com/mosip/bio-utils/blob/master/kernel-biometrics-api/src/main/java/io/mosip/kernel/biometrics/spi/IBioApiV2.java).

To know more about implementation, [refer here](biosdk-services/README.md).

## Contribution & Community

• To learn how you can contribute code to this application, [click here](https://docs.mosip.io/1.2.0/community/code-contributions).

• If you have questions or encounter issues, visit the [MOSIP Community](https://community.mosip.io/) for support.

• For any GitHub issues: [Report here](https://github.com/mosip/biosdk-services/issues)

To know more about Biometric SDK, refer [biometric-sdk](https://docs.mosip.io/1.2.0/biometrics/biometric-sdk).

### License
This project is licensed under the terms of [Mozilla Public License 2.0](LICENSE).
