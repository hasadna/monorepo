# Run LocalHttpGateway and LocalServer
# Usage:
# run.sh --root_path <root_path>

# Build and exit on fail:
bazel build @startup_os//tools/local_server:local_http_gateway
if [ $? -ne 0 ]; then
  exit $?
fi
bazel build //tools/local_server:local_server
if [ $? -ne 0 ]; then
  exit $?
fi

# Kill previous:
pkill -f 'tools/local_server/local_http_gateway'
pkill -f 'tools/local_server/local_server'
fuser -k 8000/tcp # For Angular

# We clone StartupOS to run the local Angular server. Otherwise it won't work
# (see https://github.com/alexeagle/angular-bazel-example/issues/122)
git clone https://github.com/google/startup-os.git tools/local_server/startup-os
cd tools/local_server/startup-os/tools/local_server/web_login
npm install
ng serve &
cd -

# Run:
bazel-bin/external/startup_os/tools/local_server/local_http_gateway &
bazel-bin/tools/local_server/local_server "$@"
