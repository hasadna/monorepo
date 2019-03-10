#!/bin/bash

readonly DOWNLOAD_DIR_NAME="downloads_$(date +%Y%m%d_%H%M%S)"
readonly BASE_ABS_PATH=${HOME}/base

echo "Checking for curl installing"
if [[ $(which curl; echo $?) == "1" ]]; then
    echo "Installing curl"
    sudo apt install curl
    echo "curl is installed"
fi

echo "Checking for internet connection"
http_code=$(curl -LI httpbin.org/get -o /dev/null -w '%{http_code}\n' -s)
if [[ "$http_code" != "200" ]]; then
    echo "Please ensure internet connection is available and re-run the script"
    exit 1
fi

echo "Checking for JDK"
if [[ $(which javac; echo $?) == "1" ]]; then
    echo "Installing JDK"
    sudo apt update
    sudo apt-get install default-jdk
    echo "JDK is installed"
fi

# Install Bazel
if [[ $(which bazel; echo $?) == "1" ]]; then
    echo "Installing bazel"
    mkdir ${DOWNLOAD_DIR_NAME}
    wget -O ${DOWNLOAD_DIR_NAME}/bazel.0.21.0.sh https://github.com/bazelbuild/bazel/releases/download/0.21.0/bazel-0.21.0-installer-linux-x86_64.sh
    chmod +x ${DOWNLOAD_DIR_NAME}/bazel.0.21.0.sh
    sudo bash ${DOWNLOAD_DIR_NAME}/bazel.0.21.0.sh --user
    echo "bazel is installed"
fi

# Creating base folder
echo "Creating base folder"
if [[ -d "$BASE_ABS_PATH" ]]; then
    echo "$BASE_ABS_PATH folder already exists. Make sure you don't need $BASE_ABS_PATH folder, remove it and re-run the script."
    exit 1
fi
mkdir ${BASE_ABS_PATH}

# Set up StartupOS:
git clone https://github.com/google/startup-os.git $DOWNLOAD_DIR_NAME/startup-os
cd ${DOWNLOAD_DIR_NAME}/startup-os
bazel run //tools/reviewer/aa:aa_tool -- init --base_path $BASE_ABS_PATH
cd ${BASE_ABS_PATH}/head/startup-os
./test.sh

source ~/.bashrc
aa
echo "Please sign-in at https://web-login-startupos.firebaseapp.com and then check that ~/.aa_token exists"

# Removing temporary download folder
if [[ -d "$DOWNLOAD_DIR_NAME" ]]; then
    rm -Rf ${DOWNLOAD_DIR_NAME};
fi

