#!/usr/bin/env bash

RED=$(tput setaf 1)
RESET=$(tput sgr0)

function bazel_build() {
  if [[ -z "$ANDROID_HOME" ]]; then
    # Ignore third_party and android targets
    bazel query '//... except //third_party/... except kind("android_.* rule", //...)' | xargs bazel $1
    return $?
  else
    # Ignore just third_party
    bazel query '//... except //third_party/...' | xargs bazel $1
    return $?
  fi
}

# Run with `bazel` command as param (build|test)
if [[ $1 != "build" && $1 != "test" ]]; then
  echo "$RED""Run script with 'build' or 'test' as param$RESET"
  exit 1
fi

bazel_build $1
exit $?
