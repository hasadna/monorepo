const path = require('path');
const fs = require('fs-extra');
const { exec } = require('child_process');

// From '/common' to repo root
const repoRoot = path.resolve('../');
// Absolute path to parent of the repo
const workspacePath = path.parse(repoRoot).dir;
const startupOsProtoList = [
  'tools/reviewer/reviewer.proto'
];

class Protoc {
  constructor() {
    const argPath = process.argv[2];
    if (!argPath) {
      console.error('ERROR: Please set relative path to your project');
      return;
    }
    
    this.projectPath = path.join(repoRoot, argPath);
    const projectName = path.parse(argPath).name;
    if (!fs.existsSync(this.projectPath)) {
      console.error(`ERROR: Project "${projectName}" not found`);
      return;
    }

    this.startupOsPath = path.join(workspacePath, 'startup-os');
    if (!fs.existsSync(this.startupOsPath)) {
      console.error('ERROR: startup-os not found in workspace directory');
      return;
    }

    this.projectProtoConfigPath = path.join(this.projectPath, 'proto');
    if (!fs.existsSync(this.projectProtoConfigPath + '.json')) {
      console.error('ERROR: Project does not contain proto.json');
      return;
    }

    this.start();
  }

  start() {
    this.protoImportList = [];
    this.protoFilesList = [];

    // Load proto config of the project
    const projectProtoConfig = require(this.projectProtoConfigPath);
    this.projectProtoPath = path.join(this.projectPath, projectProtoConfig.protoPath);
    this.projectFunctionsPath = path.join(this.projectProtoPath, 'functions');

    // Clean proto folder
    fs.removeSync(this.projectProtoPath);
    fs.mkdirSync(this.projectProtoPath);

    // Copy project's .proto files to proto folder
    for (const projectProtoFile of projectProtoConfig.projectProtoList) {
      this.copyPackage(path.join(this.projectPath, projectProtoFile));
    }
    // Copy repo's .proto files to proto folder
    for (const repoProtoFile of projectProtoConfig.repoProtoList) {
      this.copyPackage(path.join(repoRoot, repoProtoFile));
    }
    // Copy startup-os' .proto files to proto folder
    for (const startupOsProtoFile of startupOsProtoList) {
      this.copyPackage(path.join(this.startupOsPath, startupOsProtoFile));
    }

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

  // Copies a package to proto folder
  copyPackage(originProtoPath) {
    const filename = path.parse(originProtoPath).base;
    this.protoImportList.push(filename);
    const newPackagePath = path.join(this.projectFunctionsPath, filename);
    this.protoFilesList.push(newPackagePath);
    fs.copySync(originProtoPath, newPackagePath);
  }

  // Removes all *.proto files from the proto folder
  removeProtoFiles(error) {
    if (error) {
      throw error;
    }
    for (let protoFile of this.protoFilesList) {
      fs.removeSync(protoFile);
    }
    this.createIndexFile();
  }

  // Creates index file for proto functions
  createIndexFile() {
    let indexTS = '';
    for(let filepath of this.protoImportList) {
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
