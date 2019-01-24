# Run LocalServer

bazel build //tools/local_server:local_server
if [ $? -ne 0 ]; then
  exit $?
fi

pkill -f 'tools/local_server/local_server'

bazel-bin/tools/local_server/local_server "$@"
