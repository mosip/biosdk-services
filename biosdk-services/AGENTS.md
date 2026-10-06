# biosdk-services/

```
SdkApplication
├── MainController                 GET / · POST /init|/match|/check-quality|/extract-template|/segment|/convert-format
├── BioSdkServiceFactory           version → spec "1.0"
├── BioSdkServiceProviderImpl_V_1_0  convertFormatV2
├── BioSdkLibConfig                biosdk_bioapi_impl reflect
└── RequestDto                     Base64 request · top-level errors
```

pom: `kernel-core` · `spring-boot-jackson2` · no `kernel-bom` · ZIP `PropertiesLauncher`
run: `mvn clean install "-Dgpg.skip=true"` · `run-local.bat|sh` `init|test|run|all`
local: `-Dspring.cloud.config.enabled=false` `-Dspring.profiles.active=local` · `:9099` `/biosdk-service/` · GET `/` · `/swagger-ui.html`
loader: `.local/mock-sdk-1.4.1-SNAPSHOT-jar-with-dependencies.jar` (from local `mosip-mock-services` target)
openapi: `${mosipbox.public.url:}${server.servlet.context-path}` (relative `/biosdk-service` if public url unset) · localhost only in `application-local.properties`
