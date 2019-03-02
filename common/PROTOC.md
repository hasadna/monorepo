# How to create proto functions

## Installation
Run `npm install` to install node modules

## Setting
A project must contain `proto.json` file with paths to required files.  
Example:
```
{ 
  // Path to folder, where proto functions will be located.
  // The path is relative to the project
  "protoPath": "src/app/core/proto",
  // List with paths to each .proto file inside the project.
  // The paths are relative to the project
  "projectProtoList": [
    "proto/apples.proto"
    "proto/pineapples.proto"
  ],
  // List with paths to each .proto file inside the repo.
  // The paths are relative to the repo
  "repoProtoList": [
    "common/repo/repo.proto"
  ]
}
```

There is `shared-proto.json`, which is list of paths to each .proto file inside the workspace.  
The paths are relative to the workspace.  
(Files from projectProtoList are not contained inside repoProtoList,
and file from repoProtoList are not contained inside `shared-proto.json`)

## Running
Run `npm run protoc <relative path to your project>` to generate proto functions.  
E.g. `npm run protoc projects/angular-proto-firestore`

## Supported platforms
Supported on Linux and Mac only.
