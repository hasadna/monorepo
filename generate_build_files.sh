#!/usr/bin/env bash

bazel run @startup_os//tools/build_file_generator:build_file_generator_tool -- --build_file_generation_blacklist=projects/noloan,projects/angular,projects/noloan2
