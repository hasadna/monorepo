#!/usr/bin/env bash

RED=$(tput setaf 1)
RESET=$(tput sgr0)

CIRCLECI_MAX_ATTEMPTS=10

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

if [[ ! -z "$CIRCLECI" ]]; then
  echo "$RED""Due to flakiness in bazel execution on CircleCI, trying to build several times"
  for i in $(seq 1 ${CIRCLECI_MAX_ATTEMPTS}); do
    echo "$RED""[Attempt $i/${CIRCLECI_MAX_ATTEMPTS}]: building$RESET"
    bazel $1 //...
    if [[ $? -eq 0 ]]; then
      echo "$GREEN""[Attempt $i/${CIRCLECI_MAX_ATTEMPTS}]: successful$RESET"
      exit 0
    fi
  done

  echo "$RED""[Attempts exhausted]: Seems it's a problem with your code and not a CircleCI flake.$RESET"
  exit 1
fi

bazel_build $1
exit $?
