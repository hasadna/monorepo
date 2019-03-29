# Angular projects

To set up your development environment, follow these steps:

## Installation
Install these:
* [node](https://nodejs.org/) version 10.15.3
* [npm](https://www.npmjs.com/) version 6.4.1
* [protoc](https://github.com/protocolbuffers/protobuf/releases), version 3.6.1
* [ng](https://angular.io/), version 7.2.0

(Installation of exact versions is not required, but recommended. We can't guarantee that the apps will work properly with different versions.)

[How to install on Linux](https://github.com/google/startup-os/blob/master/tools/reviewer/webapp/how-to-linux.md) 

Optional:
* [firebase](https://firebase.google.com/docs/hosting/quickstart), to be able to deploy.

Run `npm ci` to install common node modules  

Open direct project. E.g. `cd angular-proto-firestore`.  
Run `npm ci` to install node modules inside a project  
Run `npm run protoc` to generate proto functions. [More info](https://github.com/google/startup-os/blob/master/tools/protoc/README.md).  
You need to open protoc folder and intall node modules there too.

## Development
Open direct project  
Run `npm start` to start a dev server. Navigate to `http://localhost:4200/`  
Run `npm run lint` to find tslint errors.  
Run `npm run fix` to fix tslint errors.  
Run `npm run build` to make a build.  
Run `npm run deploy` to publish the app on the server.  
Run `npm run functions` to update cloud functions on the server.  

How to use firebase hosting:  
https://firebase.google.com/docs/hosting/quickstart  

How to use firebase functions:  
https://firebase.google.com/docs/functions/get-started  

## Updating protos
After every proto update, run `npm run protoc`.

## Development
The information might be useful:
* [AngularFire](https://github.com/angular/angularfire2/tree/master/docs)
* [Protocol Buffers](https://developers.google.com/protocol-buffers/)
