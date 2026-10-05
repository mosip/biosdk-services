#!/usr/bin/env bash
# Service runner — no Docker. Windows cmd: use run-local.bat
#
#   ./run-local.sh init | test | run | all
set -euo pipefail

MODULE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MODULE="biosdk-services"
JAR_VERSION="1.4.1-SNAPSHOT"
IMPL="io.mosip.mock.sdk.impl.SampleSDKV2"
PORT="9099"
CTX="/biosdk-service"
MOCK_SDK_GAV="io.mosip.mock.sdk:mock-sdk:1.4.0-SNAPSHOT:jar:jar-with-dependencies"
LOCAL_DIR="${MODULE_DIR}/.local"
UNAME_S="$(uname -s 2>/dev/null || echo unknown)"

MVN_SKIP=(
  "-DskipTests"
  "-Dgpg.skip=true"
  "-Dmaven.javadoc.skip=true"
)

print_endpoints() {
  local base="http://localhost:${PORT}${CTX}"
  echo "    listen:     ${base}/"
  echo "    health:     ${base}/"
  echo "    actuator:   ${base}/actuator/health"
  echo "    info:       ${base}/actuator/info"
  echo "    env:        ${base}/actuator/env"
  echo "    mappings:   ${base}/actuator/mappings"
  echo "    swagger:    ${base}/swagger-ui.html"
  echo "    swagger ui: ${base}/swagger-ui/index.html"
  echo "    api-docs:   ${base}/v3/api-docs"
  echo "    POST:       ${base}/init"
  echo "                ${base}/match"
  echo "                ${base}/check-quality"
  echo "                ${base}/extract-template"
  echo "                ${base}/segment"
  echo "                ${base}/convert-format"
  echo "    impl:       ${IMPL}"
  echo "    profile:    local  (config server off)"
}

usage() {
  cat <<EOF
Local biosdk-services (REST — mock SDK on loader.path, :${PORT}${CTX}/)

  Linux / macOS / Git Bash:
    ./run-local.sh init | test | run | all

  Windows cmd:
    run-local.bat init | test | run | all

  init    Maven package this module (skip tests) and download mock SDK
  test    Maven unit tests
  run     Start the jar with mock SDK (after init)
  all     init + test

Endpoints:
EOF
  print_endpoints
  exit "${1:-0}"
}

need_cmd() {
  command -v "$1" >/dev/null 2>&1 || {
    echo "error: '$1' is required on PATH" >&2
    exit 1
  }
}

check_prereqs() {
  need_cmd java
  need_cmd mvn
  echo "os: ${UNAME_S}"
  local ver
  ver="$(java -version 2>&1 | head -n 1 || true)"
  echo "java: $ver"
  if ! echo "$ver" | grep -E '"21[\. "]' >/dev/null 2>&1; then
    echo "warn: JDK 21 is required. Continuing anyway." >&2
  fi
}

mvn_module() {
  (
    cd "$MODULE_DIR"
    mvn "$@"
  )
}

find_mock_sdk() {
  if [[ -n "${BIOSDK_MOCK_SDK:-}" && -f "$BIOSDK_MOCK_SDK" ]]; then
    printf '%s\n' "$BIOSDK_MOCK_SDK"
    return 0
  fi
  local f
  shopt -s nullglob
  for f in "$LOCAL_DIR"/mock-sdk-*-jar-with-dependencies.jar \
           "$MODULE_DIR"/mock-sdk-*-jar-with-dependencies.jar; do
    if [[ -f "$f" ]]; then
      printf '%s\n' "$f"
      shopt -u nullglob
      return 0
    fi
  done
  shopt -u nullglob
  return 1
}

download_mock_sdk() {
  local mock_sdk
  if mock_sdk="$(find_mock_sdk)"; then
    printf '%s\n' "$mock_sdk"
    return 0
  fi
  mkdir -p "$LOCAL_DIR"
  echo "==> downloading mock-sdk 1.4.0-SNAPSHOT (jar-with-dependencies)" >&2
  mvn_module -q org.apache.maven.plugins:maven-dependency-plugin:3.11.0:copy \
    "-Dartifact=${MOCK_SDK_GAV}" \
    "-DoutputDirectory=${LOCAL_DIR}" \
    "-Dgpg.skip=true"
  mock_sdk="$(find_mock_sdk)" || {
    echo "error: failed to download mock-sdk. Set BIOSDK_MOCK_SDK or check Maven snapshots." >&2
    exit 1
  }
  echo "    sdk: ${mock_sdk}" >&2
  printf '%s\n' "$mock_sdk"
}

sdk_has_spring() {
  jar tf "$1" 2>/dev/null | grep -Eq 'org/springframework/boot/context/event/ApplicationEnvironmentPreparedEvent.class|ch/qos/logback/core/model/processor/ModelInterpretationContext.class'
}

# Fat mock-sdk shades Spring Boot 3. loader.path would then hide Boot 4.1.1
# getBootstrapContext(). Strip org/springframework when that class is present.
prepare_loader_sdk() {
  local src="$1"
  if ! sdk_has_spring "$src"; then
    printf '%s\n' "$src"
    return 0
  fi
  local loader="${LOCAL_DIR}/mock-sdk-loader.jar"
  local stamp_file="${LOCAL_DIR}/mock-sdk-loader.stamp"
  local stamp
  stamp="$(basename "$src") $(wc -c <"$src" | tr -d ' ') strip-v3"
  if [[ -f "$loader" && -f "$stamp_file" && "$(cat "$stamp_file")" == "$stamp" ]]; then
    printf '%s\n' "$loader"
    return 0
  fi
  echo "==> building loader.path JAR without Spring/Tomcat/Logback (Boot 4)" >&2
  local unpack="${LOCAL_DIR}/sdk-unpacked"
  local keep="${LOCAL_DIR}/sdk-keep"
  rm -rf "$unpack" "$keep"
  mkdir -p "$unpack" "$keep"
  (cd "$unpack" && jar xf "$src")
  local d
  for d in \
    io/mosip/mock \
    io/mosip/biometrics \
    io/mosip/kernel/bio \
    org/opencv \
    nu/pattern \
    com/github/jaiimageio \
    org/imgscalr \
    jj2000 \
    org/jnbis \
    org/apache/commons/math3 \
    assets
  do
    if [[ -d "$unpack/$d" ]]; then
      mkdir -p "$keep/$(dirname "$d")"
      cp -a "$unpack/$d" "$keep/$(dirname "$d")/"
    fi
  done
  rm -f "$loader"
  (cd "$keep" && jar cf "$loader" .)
  rm -rf "$unpack" "$keep"
  printf '%s\n' "$stamp" >"$stamp_file"
  printf '%s\n' "$loader"
}

find_service_jar() {
  local preferred="$MODULE_DIR/target/${MODULE}-${JAR_VERSION}.jar"
  if [[ -f "$preferred" ]]; then
    printf '%s\n' "$preferred"
    return 0
  fi
  local f
  shopt -s nullglob
  for f in "$MODULE_DIR"/target/"${MODULE}"-*.jar; do
    case "$(basename "$f")" in
      *sources*|*javadoc*|*original*) continue ;;
    esac
    if [[ -f "$f" ]]; then
      printf '%s\n' "$f"
      shopt -u nullglob
      return 0
    fi
  done
  shopt -u nullglob
  return 1
}

cmd_init() {
  check_prereqs
  echo "==> packaging ${MODULE} (skip tests)"
  mvn_module clean package "${MVN_SKIP[@]}"
  download_mock_sdk >/dev/null
  echo "init complete"
}

cmd_test() {
  check_prereqs
  echo "==> maven unit tests"
  mvn_module test "-Dgpg.skip=true" "-Dmaven.javadoc.skip=true"
}

cmd_run() {
  check_prereqs
  local service_jar mock_sdk loader_sdk
  service_jar="$(find_service_jar)" || {
    echo "error: service jar not found. Run: ./run-local.sh init" >&2
    exit 1
  }
  mock_sdk="$(download_mock_sdk)"
  loader_sdk="$(prepare_loader_sdk "$mock_sdk")"
  mkdir -p "${LOCAL_DIR}/logs"
  echo "==> starting ${MODULE} on :${PORT}${CTX}/"
  echo "    jar: ${service_jar}"
  echo "    sdk: ${mock_sdk}"
  echo "    loader.path: ${loader_sdk}"
  echo "    log: ${LOCAL_DIR}/logs/${MODULE}.log"
  print_endpoints
  (
    cd "$MODULE_DIR"
    java \
      -Dloader.path="${loader_sdk}" \
      -Dbiosdk_bioapi_impl="${IMPL}" \
      -Dspring.cloud.config.enabled=false \
      -Dspring.profiles.active=local \
      --add-modules=ALL-SYSTEM \
      --add-opens java.xml/jdk.xml.internal=ALL-UNNAMED \
      --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
      --add-opens java.base/java.lang.stream=ALL-UNNAMED \
      --add-opens java.base/java.time=ALL-UNNAMED \
      --add-opens java.base/java.time.LocalDate=ALL-UNNAMED \
      --add-opens java.base/java.time.LocalDateTime=ALL-UNNAMED \
      --add-opens java.base/java.time.LocalDateTime.date=ALL-UNNAMED \
      -jar "${service_jar}"
  )
}

cmd_all() {
  echo "==> all: init + test"
  cmd_init
  cmd_test
}

main() {
  local cmd="${1:-}"
  shift || true
  case "$cmd" in
    -h|--help|help) usage 0 ;;
    init) cmd_init ;;
    test) cmd_test ;;
    run) cmd_run ;;
    all) cmd_all ;;
    "") usage 1 ;;
    *) echo "error: unknown command '$cmd'" >&2; usage 1 ;;
  esac
}

main "$@"
