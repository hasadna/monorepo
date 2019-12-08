# IntelliJ IDEA settings

### Installing the Bazel plugin
Install it from within the IDE (`Settings > Plugins > Marketplace`, and search for **Bazel**).
  
  If that doesn't work, see alternative option - https://ij.bazel.build/docs/bazel-plugin.html#getting-started.
  
  As of `5 March 2019`, the plugin is compatible with IntelliJ 2017.3.3 and later.

### Importing a Project
- Launch the IDE
- Choose Import Bazel project from the Welcome screen or go to File - Import Bazel Project (if some project already opened in IDE)
- First, you need to select an existing workspace root (the directory containing the WORKSPACE file).
![Screenshot 1](https://imgur.com/WqwODKI.png)
- Next, you need to choose `Create from scratch` option
![Screenshot 2](https://imgur.com/WVMThTo.png)
- Click Next then Finish to import the project into the IDE.

See more import options - https://ij.bazel.build/docs/import-project.html

### Building a Project
Run `./compile.sh build` from IDE terminal if you get a mistake during building process in the previous step.
