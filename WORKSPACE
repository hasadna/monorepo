# bazel-deps-deploy is a prebuilt version of johnynek/bazel-deps
# we cannot use it via http_archive directly for the moment
# relevant issue: https://github.com/johnynek/bazel-deps/issues/126
git_repository(
    name = 'bazel_deps',
    remote = 'https://github.com/vmax/bazel-deps-deploy',
    commit = 'a15c2f64e099e78871ee78ff1f4e6bec5ec7ed4c'
)

load("//third_party/maven:workspace.bzl", "maven_dependencies")

maven_dependencies()

http_archive(
    name = "io_grpc_grpc_java",
    sha256 = "5ba69890c9fe7bf476093d8863f26b861184c623ba43b70ef938a190cfb95bdc",
    strip_prefix = "grpc-java-1.12.0",
    urls = ["https://github.com/grpc/grpc-java/archive/v1.12.0.tar.gz"],
)

# Google Maven Repository
GMAVEN_TAG = "20180513-1"

http_archive(
    name = "gmaven_rules",
    sha256 = "da44017f6d7bc5148a73cfd9bf8dbb1ee5a1301a596edad9181c5dc7648076ae",
    strip_prefix = "gmaven_rules-%s" % GMAVEN_TAG,
    url = "https://github.com/bazelbuild/gmaven_rules/archive/%s.tar.gz" % GMAVEN_TAG,
)

load("@gmaven_rules//:gmaven.bzl", "gmaven_rules")

gmaven_rules()

# Android SDK configuration. For more details, see:
# https://docs.bazel.build/versions/master/be/android.html#android_sdk_repository
android_sdk_repository(
    name = "androidsdk",
    api_level = 27,
    build_tools_version = "27.0.3",
)


http_archive(
    name = "startup_os",
    urls = ["https://github.com/google/startup-os/archive/b347b3942bc942707dab743a80865f00fb44b8d0.zip"],
    strip_prefix = "startup-os-b347b3942bc942707dab743a80865f00fb44b8d0"
)

# XXX Use maven deps
maven_jar(
  name = "com_squareup_okhttp_okhttp_2_7_2",
  artifact = "com.squareup.okhttp:okhttp:jar:2.7.2",
)

maven_jar(
  name = "com_squareup_okio_okio_1_6_0",
  artifact = "com.squareup.okio:okio:jar:1.6.0"
)

git_repository(
    name = 'temp',
    remote = 'https://github.com/vmax/temp',
    commit = '40edea85e2c9029de00a18e51a0b70515777ac64'
)

http_file(
    name = "buildifier",
    executable = True,
    sha256 = "d7d41def74991a34dfd2ac8a73804ff11c514c024a901f64ab07f45a3cf0cfef",
    urls = ["https://github.com/bazelbuild/buildtools/releases/download/0.11.1/buildifier"],
)

http_file(
    name = "buildifier_osx",
    executable = True,
    sha256 = "3cbd708ff77f36413cfaef89cd5790a1137da5dfc3d9b3b3ca3fac669fbc298b",
    urls = ["https://github.com/bazelbuild/buildtools/releases/download/0.11.1/buildifier.osx"],
)

http_file(
    name = "protoc_bin",
    executable = True,
    sha256 = "84e29b25de6896c6c4b22067fb79472dac13cf54240a7a210ef1cac623f5231d",
    urls = ["https://github.com/google/protobuf/releases/download/v3.6.0/protoc-3.6.0-linux-x86_64.zip"]
)

http_file(
    name = "protoc_bin_osx",
    executable = True,
    sha256 = "768a42032718accd12e056447b0d93d42ffcdc27d1b0f21fc1e30a900da94842",
    urls = ["https://github.com/google/protobuf/releases/download/v3.6.0/protoc-3.6.0-osx-x86_64.zip"]
)

bind(
    name = "proto_compiler",
    actual = "@startup_os//tools:protoc"
)

bind(
    name = "proto_java_toolchain",
    actual = "@startup_os//tools:java_toolchain"
)

bind(
    name = "grpc_java_plugin",
    actual = "@temp//:grpc_java_plugin"
)
