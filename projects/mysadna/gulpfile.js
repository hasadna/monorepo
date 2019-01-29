const path = require('path');
const fs = require('fs-extra');  
const gulp = require('gulp');
const clean = require('gulp-clean');
const { exec } = require('child_process');

const protoImportPathList = [
  './proto/data.proto',
];
const exportProtoPath = './src/app/proto';
const functionsPath = path.join(exportProtoPath, 'functions');

gulp.task('protoc', (done) => {
  let protoImportList = [];
  
  // Remove old functions (if they exist)
  fs.exists(exportProtoPath, exists => {
    if (exists) {
      gulp.src(exportProtoPath)
        .pipe(clean())
        .on('data', () => {})
        .on('end', createFunctions);
    } else {
      createFunctions();
    }
  });
  
  function createFunctions() {
    // Create a proto folder
    fs.mkdirSync(exportProtoPath);

    // Copy all packages to the proto folder
    function copyPackage(relativePath) {
      const filename = path.parse(relativePath).base;
      protoImportList.push(filename);
      const newPackagePath = path.join(functionsPath, filename);
      fs.copySync(relativePath, newPackagePath);

      return newPackagePath;
    }

    for (const importPathList of protoImportPathList) {
      copyPackage(importPathList);
    }

    // Create proto functions from the packages in the proto folder
    const protoImport = protoImportList.join(' ');
    exec(
      'protoc ' +
        `--proto_path=${functionsPath} ` +
        `--js_out=import_style=commonjs,binary:${functionsPath} ` +
        '--plugin=protoc-gen-ts=./node_modules/.bin/protoc-gen-ts ' +
        `--ts_out=${functionsPath} ` +
        protoImport,
      removeProtoFiles
    );
  }

  // Removes all *.proto files from the proto folder
  function removeProtoFiles(error) {
    if (error) {
      throw error;
    }

    gulp.src(path.join(functionsPath, '**/*.proto'))
      .pipe(clean())
      .on('data', () => {})
      .on('end', createIndexFile);
  }

  // Creates index file for proto functions
  function createIndexFile() {
    let indexTS = '';
    for(let filepath of protoImportList) {
      const dir = path.parse(filepath).dir;
      const filename = path.parse(filepath).name + '_pb';
      const indexpath = path.join(dir, filename);
      indexTS += "export * from './functions/" + indexpath + "';\n";
    }
    fs.writeFileSync(path.join(exportProtoPath, 'index.ts'), indexTS);

    onLoad();
  }

  function onLoad() {
    console.log('Proto functions are successfully created.');
    done();
  }
});
