#!/usr/bin/env bash

# Run it before committing to fix formatting
# on all files so you won't fail early on review
# Execute from repo root or, if using aa from base/head/startup-os

RED=$(tput setaf 1)
RESET=$(tput sgr0)

npm install &>/dev/null

bazel run @startup_os//tools/formatter -- \
	--path $(pwd) \
	--java --proto --cpp --build \
	--ignore_directories $(find $(pwd) -name node_modules -type d | paste -s -d , -) \
	&>/dev/null

# Prints out an error only if both conditions are satisfied:
# * we are on CircleCI
# * working tree contains unstaged changes
# When ran locally it silently fixes everything.
if [[ ! -z "$CIRCLECI" && ! -z $(git status -s) ]]; then
	echo "$RED[!] Source files are not formatted:$RESET";
	git status
	echo "Please run ''./fix-formatting.sh'' to fix it"
	exit 1
fi
