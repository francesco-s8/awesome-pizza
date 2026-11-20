#!/bin/sh
set -eu

AOT_DIR=/application/aot-cache
AOT_CONFIG="$AOT_DIR/application.aotconf"
AOT_CACHE="$AOT_DIR/application.aot"
CLASSPATH="/application/*:/application/lib/*"

set -- \
  -XX:+UseZGC \
  -XX:+UseCompactObjectHeaders \
  -XX:+UseStringDeduplication \
  -XX:+UseCompressedOops \
  -XX:+UseCompressedClassPointers

case "${AOT_MODE:-use}" in
  record)
    set -- "$@" -XX:AOTMode=record "-XX:AOTConfiguration=$AOT_CONFIG"
    exec java "$@" -cp "$CLASSPATH" it.s8.awesomepizza.AwesomePizzaApplication
    ;;
  create)
    if [ ! -f "$AOT_CONFIG" ]; then
      echo "AOT configuration not found at $AOT_CONFIG; run with AOT_MODE=record first." >&2
      exit 1
    fi
    set -- "$@" -XX:AOTMode=create "-XX:AOTConfiguration=$AOT_CONFIG" "-XX:AOTCache=$AOT_CACHE"
    exec java "$@"
    ;;
  use)
    if [ -f "$AOT_CACHE" ]; then
      set -- "$@" "-XX:AOTCache=$AOT_CACHE"
    fi
    exec java "$@" -cp "$CLASSPATH" it.s8.awesomepizza.AwesomePizzaApplication
    ;;
  *)
    echo "Unsupported AOT_MODE: ${AOT_MODE}. Use record, create, or use." >&2
    exit 64
    ;;
esac
