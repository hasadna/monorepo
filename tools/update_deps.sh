#!/usr/bin/env bash

# Script for updating Maven deps after modifying or deleting a dependency in dependencies.yaml.
# Usage: tools/deps/update_maven_deps.sh
# TODO: Rename workspace.bzl to package-lock.bzl
# TODO: Think of how to use StartupOS's update_deps.sh.
bazel run @startup_os//tools:bazel_deps -- generate -r $(pwd) -s third_party/maven/workspace.bzl -d dependencies.yaml

# Fix formatting for BUILD files
bazel run @startup_os//tools/formatter -- --path $(pwd)/third_party --build &>/dev/null