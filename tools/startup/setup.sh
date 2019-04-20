
#!/bin/bash

# Setup script. Please connect to wifi before running (see TODO below).
# Note: Because of the FIXME, you'll have to run it, run "source ~/.bashrc", and run it again.

# Make us the owners of the files in data
sudo chown -R $USER /media/ubuntu/data
cd /media/$USER/data

if [ ! -d downloads ]; then
  mkdir downloads
fi

# Uninstall unneeded packages
sudo apt-get purge -y thunderbird*
sudo apt-get purge -y firefox*
sudo apt-get purge -y libreoffice*
sudo apt-get purge -y gnome-sudoku
sudo apt-get purge -y gnome-mines
sudo apt-get purge -y gnome-mahjongg
sudo apt-get purge -y gnome-calendar
sudo apt-get purge -y *gnome-todo*
sudo apt-get purge -y transmission*
sudo apt-get purge -y deja-dup
sudo apt-get purge -y aisleriot
sudo apt-get purge -y *mozc*
sudo apt-get purge -y *rhythmbox* gir1.2-rb-3.0:amd64
sudo apt-get purge -y ubuntu-web-launchers

# Copy wifi credentials
mkdir -p wifis
sudo cp /etc/NetworkManager/system-connections/* wifis/
sudo cp wifis/* /etc/NetworkManager/system-connections/
echo "Restarting system network service"
sudo systemctl restart NetworkManager

echo "Waiting for internet connection to settle"
sleep 10

echo "Checking for internet connection"
http_code=$(curl -LI httpbin.org/get -o /dev/null -w '%{http_code}\n' -s)
if [[ "$http_code" != "200" ]]; then
    echo "Please ensure internet connection is available and re-run the script"
    exit 1
fi

# Download apt installs if needed
# TODO: Figure out how to download packages from a predefined list, instead of "latest". This list will be generated from "latest" at some point in time.
if [ ! -d downloads/apt ]; then
  mkdir downloads/apt
  curl https://packages.microsoft.com/keys/microsoft.asc | gpg --dearmor > downloads/microsoft.gpg
  sudo apt-get -dy update
  sudo apt-get -fdy install git default-jdk pkg-config zip g++ zlib1g-dev unzip python python3 curl apt-transport-https
  sudo apt-get -dy update
  wget -O downloads/gconf2-common_3.2.6-4ubuntu1_all.deb http://archive.ubuntu.com/ubuntu/pool/universe/g/gconf/gconf2-common_3.2.6-4ubuntu1_all.deb
  wget -O downloads/libgconf-2-4_3.2.6-4ubuntu1_amd64.deb http://archive.ubuntu.com/ubuntu/pool/universe/g/gconf/libgconf-2-4_3.2.6-4ubuntu1_amd64.deb
  sudo apt-get install -fdy code
  sudo apt-get -dy upgrade
  # TODO: Figure out how to cache the "apt-get update" to the downloads folder.
  cp /var/cache/apt/archives/*.deb downloads/apt
fi
sudo cp downloads/apt/*.deb /var/cache/apt/archives/
sudo apt-get install -y git default-jdk pkg-config zip g++ zlib1g-dev unzip python python3 curl apt-transport-https
sudo dpkg -i downloads/gconf2-common_3.2.6-4ubuntu1_all.deb downloads/libgconf-2-4_3.2.6-4ubuntu1_amd64.deb
sudo install -o root -g root -m 644 downloads/microsoft.gpg /etc/apt/trusted.gpg.d/
sudo sh -c 'echo "deb [arch=amd64] https://packages.microsoft.com/repos/vscode stable main" > /etc/apt/sources.list.d/vscode.list'
sudo apt-get -y update
sudo apt-get install -y code
sudo apt-get -y upgrade

# Install Chrome
if [ ! -f downloads/chrome.deb ]; then
  wget -O downloads/chrome.deb https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
fi
sudo dpkg -i downloads/chrome.deb

# Install Bazel
if [ ! -f downloads/bazel.0.21.0.sh ]; then
  wget -O downloads/bazel.0.21.0.sh https://github.com/bazelbuild/bazel/releases/download/0.21.0/bazel-0.21.0-installer-linux-x86_64.sh
  chmod +x downloads/bazel.0.21.0.sh
fi
sudo bash downloads/bazel.0.21.0.sh --user

# Install IntelliJ
if [ ! -f downloads/idea.2018.3.3.tar.gz ]; then
  wget -O downloads/idea.2018.3.3.tar.gz https://download.jetbrains.com/idea/ideaIC-2018.3.3.tar.gz
  rm -rf opt/idea
  mkdir -p opt/idea
  tar xfz downloads/idea.2018.3.3.tar.gz -C opt/idea --strip 1
fi

# Set up desktop defaults
gsettings set org.gnome.shell favorite-apps "['google-chrome.desktop', 'code.desktop', 'org.gnome.Nautilus.desktop', 'org.gnome.gedit.desktop', 'org.gnome.Terminal.desktop']"
gsettings set org.gnome.desktop.interface show-battery-percentage true
gsettings set org.gnome.desktop.interface enable-animations false
gsettings set org.gnome.gedit.preferences.editor auto-save true
gsettings set org.gnome.gedit.preferences.editor display-line-numbers true
gsettings set org.gnome.gedit.preferences.editor insert-spaces true
gsettings set org.gnome.gedit.preferences.editor tabs-size 2
gsettings set org.gnome.nautilus.preferences show-hidden-files true
gsettings set org.gtk.Settings.FileChooser show-hidden true
rm -rf ~/Public
rm ~/examples.desktop
rm ~/Desktop/ubiquity.desktop 
sudo touch ~/Templates/"Empty file.txt"

# Set up .bashrc:
echo "source /media/$USER/data/dotfiles/.terminal" >> ~/.bashrc
# FIXME: Why does it not add to PATH:
source ~/.bashrc

# Set up StartupOS:
git clone https://github.com/google/startup-os.git downloads/startup-os
cd downloads/startup-os
bazel run //tools/reviewer/aa:aa_tool -- init --base_path /media/$USER/data/base
cd /media/$USER/data/base/head/startup-os
./test.sh
# FIXME: Why does it not add to PATH:
source ~/.bashrc
# TODO: Add aa startserver to be explicit about doing just that action.
aa
echo "Please sign-in at https://web-login-startupos.firebaseapp.com and then check that ~/.aa_token exists"
cd /media/$USER/data
# TODO: Figure out where Chrome, vscode and Idea store configs, to avoid first-use windows.
# TODO: Install Bazel plugin in vscode and Idea
# TODO: Add bazel caches to data (at bazel_cache/downloads and bazel_cache/builds)
