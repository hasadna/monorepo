const path = require('path');
const fs = require('fs-extra');
const { exec } = require('child_process');

const sharedProtoList = require('./shared-proto');
// Since the script is located here: '/common'
const repoRoot = path.resolve('../');
// Absolute path to parent of the repo
const workspacePath = path.parse(repoRoot).dir;

class Protoc {
  constructor() {
    const argPath = process.argv[2];
    if (!argPath) {
      this.error('Please set relative path to your project');
    }

    this.projectPath = path.join(repoRoot, argPath);
    const projectName = path.parse(argPath).name;
    if (!fs.existsSync(this.projectPath)) {
      this.error(`Project "${projectName}" not found`);
    }

    for (const shareProtoFile of sharedProtoList) {
      if (!fs.existsSync(path.join(workspacePath, shareProtoFile))) {
        this.error(`${shareProtoFile} not found in workspace directory`);
      }
    }

    this.projectProtoConfigPath = path.join(this.projectPath, 'proto');
    if (!fs.existsSync(this.projectProtoConfigPath + '.json')) {
      this.error('Project does not contain proto.json');
    }

    this.start();
  }

  start() {
    this.protoImportList = [];
    this.protoFilesList = [];

    // Load proto config of the project
    const projectProtoConfig = require(this.projectProtoConfigPath);
    if (!projectProtoConfig.protoPath) {
      this.error('Path to proto functions is not set');
    }
    this.projectProtoPath = path.join(this.projectPath, projectProtoConfig.protoPath);
    this.projectFunctionsPath = path.join(this.projectProtoPath, 'functions');

    // Clean proto folder
    fs.removeSync(this.projectProtoPath);
    fs.mkdirSync(this.projectProtoPath);

    // Copy project's .proto files to proto folder
    if (projectProtoConfig.projectProtoList) {
      for (const projectProtoFile of projectProtoConfig.projectProtoList) {
        this.copyPackage(path.join(this.projectPath, projectProtoFile));
      }
    }
    // Copy repo's .proto files to proto folder
    if (projectProtoConfig.repoProtoList) {
      for (const repoProtoFile of projectProtoConfig.repoProtoList) {
        this.copyPackage(path.join(repoRoot, repoProtoFile));
      }
    }
    // Copy shared .proto files to proto folder
    for (const shareProtoFile of sharedProtoList) {
      this.copyPackage(path.join(workspacePath, shareProtoFile));
    }

    // Remove duplicate proto paths, in case of same proto was imported twice
    this.protoImportList = this.protoImportList.filter(
      (item, index) => this.protoImportList.indexOf(item) == index
    );

    // Create proto functions from the .proto files in the proto folder
    const protoImport = this.protoImportList.join(' ');
    exec(
      'protoc ' +
      `--proto_path=${this.projectFunctionsPath} ` +
      `--js_out=import_style=commonjs,binary:${this.projectFunctionsPath} ` +
      '--plugin=protoc-gen-ts=./node_modules/.bin/protoc-gen-ts ' +
      `--ts_out=${this.projectFunctionsPath} ` +
      protoImport, () => {
        this.removeProtoFiles();
      }
    );
  }

  error(message) {
    console.error('ERROR: ' + message);
    process.exit();
  }

  // Copies a package to proto folder
  copyPackage(originProtoPath) {
    if (!fs.existsSync(originProtoPath)) {
      this.error(originProtoPath + ' not found');
    }
    const filename = path.parse(originProtoPath).base;
    this.protoImportList.push(filename);
    const newPackagePath = path.join(this.projectFunctionsPath, filename);
    this.protoFilesList.push(newPackagePath);
    fs.copySync(originProtoPath, newPackagePath);
    this.importPackages(originProtoPath, newPackagePath);
  }

  // Reads a file and copies its import to proto folder
  importPackages(protoPath, newPackagePath) {
    const protoContent = fs.readFileSync(protoPath, 'utf8');
    let protoLines = protoContent.split('\n');
    let doesProtoContainImports = false;
    for (const lineNumber in protoLines) {
      const line = protoLines[lineNumber];
      // Get children paths from imports in code_review.proto
      if (line.startsWith('import')) {
        // Everything between quotes
        const regexExpression = /"(.*)"/;
        const importRelativePath = line.match(regexExpression)[1];

        if (importRelativePath.startsWith('google')) {
          // protoc is able to work with google imports by itself.
          // Unfortunately on unix system only.
          continue;
        }

        // Since all files are located in a same directory,
        // replace each relative import path to filename.
        // Example:
        // /path/to/my/file.proto -> file.proto
        doesProtoContainImports = true;
        const filename = path.parse(importRelativePath).base;
        protoLines[lineNumber] = `import "${filename}";`;

        const projectName = path.relative(workspacePath, protoPath).split('/')[0];
        const importPath = path.join(workspacePath, projectName, importRelativePath);
        this.copyPackage(importPath);
      }
    }
    if (doesProtoContainImports) {
      // No need to rewrite file, if it doesn't have imports
      fs.writeFileSync(newPackagePath, protoLines.join('\n'));
    }
  }

  // Removes all *.proto files from the proto folder
  removeProtoFiles(error) {
    if (error) {
      throw error;
    }
    for (const protoFile of this.protoFilesList) {
      fs.removeSync(protoFile);
    }
    this.createIndexFile();
  }

  // Creates index file for proto functions
  createIndexFile() {
    let indexTS = '';
    for (const filepath of this.protoImportList) {
      const dir = path.parse(filepath).dir;
      const filename = path.parse(filepath).name + '_pb';
      const indexpath = path.join(dir, filename);
      indexTS += "export * from './functions/" + indexpath + "';\n";
    }
    fs.writeFileSync(path.join(this.projectProtoPath, 'index.ts'), indexTS);

    this.onLoad();
  }

  onLoad() {
    console.log('Proto functions are successfully created.');
  }
}
const protoc = new Protoc();
