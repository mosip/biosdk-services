# biosdk-services

IBioApiV2 REST · JDK21 · Boot 4.1.1

```
.
├── biosdk-services/   Java ZIP + loader.path
├── .github/           CI
├── helm/              chart
└── deploy/            cluster
```

`cd biosdk-services && mvn clean install "-Dgpg.skip=true"`
`run-local.bat|sh` `init|test|run|all` · `:9099` `/biosdk-service/` · local: no Docker, no config server

work: 1 folder → that `AGENTS.md` → Grep/Glob → short read → answer
no: subagents · walks · README LICENSE NOTICE THIRD-PARTY* target/ .local logs apidocs `*.iso` jars zips

freeze: `IBioApiV2` · Base64 `request` · top-level `errors` · `convertFormatV2` · `biosdk_bioapi_impl`+`loader.path` · spec `"1.0"` · ZIP · `spring-boot-jackson2` · no `kernel-bom` · no GPG secrets
