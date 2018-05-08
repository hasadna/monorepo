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

git_repository(
    name = 'gmaven_rules',
    remote = 'https://github.com/aj-michael/gmaven_rules',
    commit = '5e89b7cdc94d002c13576fad3b28b0ae30296e55',
)
load('@gmaven_rules//:gmaven.bzl', 'gmaven_rules')
gmaven_rules()

# Set ANDROID_HOME environment variable to location of your Android SDK
# i.e. on macOS with Android Studio this would be "$HOME/Library/Android/sdk"
# XXX - fix
#android_sdk_repository(
#    name = "androidsdk",
    # specify params to have reproducible builds
    # by default bazel uses the latest available versions
    # api_level = <…>,
    # build_tools_version = <…>,
#)
android_sdk_repository(
    name = "androidsdk",
    path = "/home/oferb/Android/Sdk",
    api_level = 27,
    build_tools_version = "27.0.3"
)

http_archive(
    name = "startup_os",
    urls = ["https://github.com/google/startup-os/archive/99d78d83529a6010ecc465e598854ef03a4453d1.zip"],
    strip_prefix = "startup-os-99d78d83529a6010ecc465e598854ef03a4453d1"
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
http_archive(
    name = "com_google_protobuf",
    urls = ["https://github.com/google/protobuf/archive/3.5.1.1.zip"],
    strip_prefix = "protobuf-3.5.1.1",
)

http_archive(
    name = "com_google_protobuf_javalite",
    strip_prefix = "protobuf-javalite",
    urls = ["https://github.com/google/protobuf/archive/javalite.zip"],
)

new_http_archive(
    name = "auto_value",
    url = "http://repo1.maven.org/maven2/com/google/auto/value/auto-value/1.5.3/auto-value-1.5.3.jar",
    build_file_content = """
java_import(
    name = "jar",
    jars = ["auto-value-1.5.3.jar"],
)
 
java_plugin(
    name = "plugin",
    generates_api = 1,
    processor_class = "com.google.auto.value.processor.AutoValueProcessor",
    deps = [":jar"],
)
 
java_library(
    name = "processor",
    exported_plugins = [":plugin"],
    exports = [":jar"],
    visibility = ["//visibility:public"],
)
""")

git_repository(
  name = "org_pubref_rules_protobuf",
  remote = "https://github.com/pubref/rules_protobuf",
  tag = "v0.8.2",
)

load("@org_pubref_rules_protobuf//java:rules.bzl", "java_proto_repositories")
java_proto_repositories(excludes = [
    "com_google_protobuf",
    "com_google_api_grpc_proto_google_common_protos",
    "com_google_code_gson_gson",
    "com_google_errorprone_error_prone_annotations",
    "com_google_guava_guava",
    "com_google_protobuf_protobuf_java",
    "com_google_protobuf_protobuf_java_util",
    "io_grpc_grpc_context",
    "io_grpc_grpc_core",
    "io_grpc_grpc_netty",
    "io_grpc_grpc_protobuf",
    "io_grpc_grpc_protobuf_lite",
    "io_grpc_grpc_stub",
    "io_netty_netty_buffer",
    "io_netty_netty_codec",
    "io_netty_netty_codec_http",
    "io_netty_netty_codec_http2",
    "io_netty_netty_codec_socks",
    "io_netty_netty_common",
    "io_netty_netty_handler",
    "io_netty_netty_handler_proxy",
    "io_netty_netty_resolver",
    "io_netty_netty_transport",
    "io_opencensus_opencensus_api",
])
