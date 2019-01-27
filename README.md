# Hasadna monorepo

[![CircleCI](https://circleci.com/gh/hasadna/hasadna.svg?style=svg)](https://circleci.com/gh/hasadna/hasadna)

## Installation
Install [Bazel](https://docs.bazel.build/versions/master/install.html). That's it!

## Build & Test
* Build everything: `./compile.sh build`
* Run all tests: `./compile.sh test`

## About monorepos
A monorepo is a software development approach where all code is stored in a single repository. Some things are easier to do in a monorepo, such as sharing a proto file across front-end and backend, some things are harder, such as per-repo control over collaborators, email notifications, commit history etc.

Some monorepo etiquette:
1. Don't store large files in the repo (>500kb), or many small files. Until we figure out a way to deal with them, you can store them in a different repo.
2. If you use code belonging to another project, talk to the people of that project about it. If you don't, they might accidentally break your code.
3. If you change code common to multiple projects, do it thoughfully.
4. Kindly provide a LICENSE file for your project.

Some good reads about the monorepo approach:
* [trunkbaseddevelopment.com/monorepos](https://trunkbaseddevelopment.com/monorepos/)
* [Why Google stores billions of lines of code in a single repository](https://cacm.acm.org/magazines/2016/7/204032-why-google-stores-billions-of-lines-of-code-in-a-single-repository/fulltext)

## Platforms
While Bazel supports Linux, Mac and Windows, this repo supports Linux and Mac.
If you're on Windows 10, you can use [Windows Subsystem for Linux (WSL)](https://docs.microsoft.com/ru-ru/windows/wsl/about).
It gives you a Linux environment, without the overhead of a virtual machine.

#### Installing Windows Subsystem for Linux:
Please follow the [guide](https://docs.microsoft.com/ru-ru/windows/wsl/install-win10).
Note: You may have to do a Windows upgrade. If you need it, it will ask for it at the beginning of the installation.

## Contributing
You're welcome to contribute and in doing so, learn these technologies.
You can have a look at the issues list, or at the project [milestones](docs/milestones.md).


Happy coding!
