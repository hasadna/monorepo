load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_file")
load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_jar")
load("//third_party/maven:workspace.bzl", "maven_dependencies")

RULES_JVM_EXTERNAL_TAG = "1.0"

RULES_JVM_EXTERNAL_SHA = "48e0f1aab74fabba98feb8825459ef08dcc75618d381dff63ec9d4dd9860deaa"

maven_dependencies()

http_archive(
    name = "io_grpc_grpc_java",
    sha256 = "48425cd631afb117fd355fd961deb313b3ac8e43f2b95c1598f35fbfcf684fbc",
    strip_prefix = "grpc-java-1.16.1",
    urls = ["https://github.com/grpc/grpc-java/archive/v1.16.1.tar.gz"],
)

# Google Maven Repository
GMAVEN_TAG = "20180513-1"

http_archive(
    name = "gmaven_rules",
    sha256 = RULES_JVM_EXTERNAL_SHA,
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
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

# MARK: StartupOS start
http_archive(
    name = "startup_os",
    strip_prefix = "startup-os-87ba1ec33bc45fea4af0a1ab33e704ef58e688a2",
    urls = ["https://github.com/google/startup-os/archive/87ba1ec33bc45fea4af0a1ab33e704ef58e688a2.zip"],
)
# MARK: StartupOS end

# StartupOS dependencies start
load("@startup_os//third_party/maven:package-lock.bzl", maven_dependencies_startup_os = "maven_dependencies")

# TODO: does the order of `maven_dependencies()` and `maven_dependencies_startup_os()` in `WORKSPACE` dictate which version we will use?
maven_dependencies_startup_os()

http_archive(
    name = "io_bazel_rules_docker",
    strip_prefix = "rules_docker-0.5.1",
    urls = ["https://github.com/bazelbuild/rules_docker/archive/v0.5.1.tar.gz"],
)

http_jar(
    name = "bazel_deps",
    sha256 = "98b05c2826f2248f70e7356dc6c78bc52395904bb932fbb409a5abf5416e4292",
    urls = ["https://github.com/oferb/startupos-binaries/releases/download/0.1.01/bazel_deps.jar"],
)
# StartupOS dependencies end

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
    sha256 = "6003de742ea3fcf703cfec1cd4a3380fd143081a2eb0e559065563496af27807",
    urls = ["https://github.com/google/protobuf/releases/download/v3.6.1/protoc-3.6.1-linux-x86_64.zip"],
)

http_file(
    name = "protoc_bin_osx",
    executable = True,
    sha256 = "0decc6ce5beed07f8c20361ddeb5ac7666f09cf34572cca530e16814093f9c0c",
    urls = ["https://github.com/google/protobuf/releases/download/v3.6.1/protoc-3.6.1-osx-x86_64.zip"],
)

bind(
    name = "proto_compiler",
    actual = "@startup_os//tools:protoc",
)

bind(
    name = "proto_java_toolchain",
    actual = "@startup_os//tools:java_toolchain",
)

bind(
    name = "grpc_java_plugin",
    actual = "@startup_os//tools:grpc_java_plugin",
)

http_file(
    name = "grpc_java_plugin_linux",
    executable = True,
    sha256 = "cdd93cdf24d11ccd7bad6a4d55c9bbe55e776c3972ef177974512d5aa58debd7",
    urls = ["https://github.com/oferb/startupos-binaries/releases/download/0.1.02/grpc_java_plugin_linux"],
)

http_file(
    name = "grpc_java_plugin_osx",
    executable = True,
    sha256 = "e69af502d906199675454ac8af7dfddff78e6213df9abc63434c522adea6c6fb",
    urls = ["https://github.com/oferb/startupos-binaries/releases/download/0.1.0/grpc_java_plugin_osx"],
)

http_file(
    name = "clang_format_bin",
    executable = True,
    sha256 = "320f62a8a20941b7d876c09de96913e0d18f0e2649688c2cd010a5f12b5d7616",
    urls = ["https://github.com/oferb/startupos-binaries/releases/download/0.1.0/clang_format_bin"],
)

http_file(
    name = "clang_format_bin_osx",
    executable = True,
    sha256 = "06986eeed23213c5b6a97440c6a3090eabc62ceaf7fcb72f2b95c4744128dccf",
    urls = ["https://github.com/oferb/startupos-binaries/releases/download/0.1.0/clang_format_bin_osx"],
)

http_file(
    name = "shfmt",
    executable = True,
    sha256 = "bdf8e832a903a80806b93a9ad80d8f95a70966fbec3258a565ed5edc2ae5bcdc",
    urls = ["https://github.com/mvdan/sh/releases/download/v2.6.2/shfmt_v2.6.2_linux_amd64"],
)

http_file(
    name = "shfmt_osx",
    executable = True,
    sha256 = "aaaa7d639acb30853e2f5008f56526c8dd54a366219ebdc5fa7f13a15277dd0b",
    urls = ["https://github.com/mvdan/sh/releases/download/v2.6.2/shfmt_v2.6.2_darwin_amd64"],
)

# MARK: sample data for analysis pipeline start
http_archive(
    name = "step1_data",
    build_file_content = 'exports_files(["file1.txt", "file2.txt"])',
    sha256 = "23927505626ebdb8e17f64368ed8b8f47e1bd5baa4b8e6d9c1f25de045589f11",
    strip_prefix = "example_input_data_step1",
    url = "https://github.com/hasadna/hasadna/releases/download/v1/example_input_data_step1.zip",
)

http_file(
    name = "step1_prebuilt_output",
    urls = ["https://github.com/hasadna/hasadna/releases/download/v1/merged_file1_file2.prototxt"],
)

http_archive(
    name = "step1_prebuilt_zipped_output",
    build_file_content = 'exports_files(["merged_file1_file2.prototxt"])',
    sha256 = "08e5549daf5067079409fc31d8d1f3c5686a15c9da664f37464f2d7e3ba7c83b",
    url = "https://github.com/hasadna/hasadna/releases/download/v1/merged_file1_file2.prototxt.zip",
)
# MARK: sample data for analysis pipeline end

#gtfs_data
http_archive(
    name = "gtfs_data",
    build_file_content = 'exports_files(["calendar.txt", "routes.txt", "stop_times.txt", "stops.txt", "translations.txt", "trips.txt"])',
    url = "https://firebasestorage.googleapis.com/v0/b/startupos-5f279.appspot.com/o/israel-public-transportation.zip?alt=media&token=a9bc43a5-36a6-4126-9e19-2e42f9ff663c",
)

#israel_railways_data
http_archive(
    name = "israel_railways_data",
    build_file_content = 'exports_files(["routes.csv" , "samples.csv" , "stops.csv" , "trips.csv"])',
    url = "https://firebasestorage.googleapis.com/v0/b/startupos-5f279.appspot.com/o/data_csv.zip?alt=media&token=2a262967-7cdd-4934-afbc-8fa31a74baf2",
)
