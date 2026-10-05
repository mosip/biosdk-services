[![Maven Package upon a push](https://github.com/mosip/biosdk-services/actions/workflows/push-trigger.yml/badge.svg?branch=develop)](https://github.com/mosip/biosdk-services/actions/workflows/push-trigger.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?branch=develop&project=mosip_biosdk-services&metric=alert_status)](https://sonarcloud.io/dashboard?branch=develop&id=mosip_biosdk-services)
# Bio-SDK Service
## Overview
This service wraps MOSIP [`IBioApiV2`](https://github.com/mosip/bio-utils/blob/master/kernel-biometrics-api/src/main/java/io/mosip/kernel/biometrics/spi/IBioApiV2.java) and exposes it over HTTP. A vendor (or mock) SDK JAR is loaded at startup via `-Dloader.path` and `-Dbiosdk_bioapi_impl`. Host applications — typically [biosdk-client](https://github.com/mosip/biosdk-client) — call it for 1:N match, segmentation, extraction, quality check, and format convert.

To know more about Biometric SDK Specification, refer [here](https://docs.mosip.io/1.2.0/id-lifecycle-management/supporting-components/biometrics/biometric-sdk).

To know more about implementation, refer [here](biosdk-services/README.md).

Module runner (`init` / `test` / `run` / `all`): [`biosdk-services/run-local.sh`](biosdk-services/run-local.sh) · [`biosdk-services/README.md`](biosdk-services/README.md).

Local work is Maven + JUnit + `run-local` (no Docker, no config server). Cluster install uses Helm / Docker — see [`deploy/`](deploy/) and [`helm/biosdk-service/`](helm/biosdk-service/).

## Contribution & Community

• To learn how you can contribute code to this application, [click here](https://docs.mosip.io/1.2.0/community/code-contributions).

• If you have questions or encounter issues, visit the [MOSIP Community](https://community.mosip.io/) for support.

• For any GitHub issues: [Report here](https://github.com/mosip/biosdk-services/issues)


### License
This project is licensed under the terms of [Mozilla Public License 2.0](LICENSE).

Third-party components are listed with the JAR versions resolved from this build in [THIRD-PARTY-NOTICES.txt](THIRD-PARTY-NOTICES.txt). Approved licenses (compatible with MPL 2.0 **and** allowed with MOSIP) are in [licenses/](licenses/). Two Boot/kernel transitives (`aspectjweaver`, `jakarta.annotation-api`) are called out there because their licenses are not on the MOSIP Yes list.
