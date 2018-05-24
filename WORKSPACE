# bazel-deps-deploy is a prebuilt version of johnynek/bazel-deps
# we cannot use it via http_archive directly for the moment
# relevant issue: https://github.com/johnynek/bazel-deps/issues/126
git_repository(
    name = 'bazel_deps',
    remote = 'https://github.com/vmax/bazel-deps-deploy',
    commit = 'a15c2f64e099e78871ee78ff1f4e6bec5ec7ed4c',
)

load("//third_party/maven:workspace.bzl", "maven_dependencies")
maven_dependencies()

http_archive(
    name = "io_grpc_grpc_java",
    urls = ["https://github.com/grpc/grpc-java/archive/v1.12.0.tar.gz"],
    strip_prefix = "grpc-java-1.12.0",
)

load("@io_grpc_grpc_java//:repositories.bzl", "grpc_java_repositories")

# TODO: Figure out if we also need omit_com_google_protobuf_javalite = True
grpc_java_repositories(
    omit_com_google_api_grpc_google_common_protos = True,
    omit_com_google_auth_google_auth_library_credentials = True,
    omit_com_google_code_findbugs_jsr305 = True,
    omit_com_google_code_gson = True,
    omit_com_google_errorprone_error_prone_annotations = True,
    omit_com_google_guava = True,
    omit_com_google_protobuf = True,
    omit_com_google_protobuf_nano_protobuf_javanano = True,
    omit_com_google_truth_truth = True,
    omit_com_squareup_okhttp = True,
    omit_com_squareup_okio = True,
    omit_io_netty_buffer = True,
    omit_io_netty_codec = True,
    omit_io_netty_codec_http = True,
    omit_io_netty_codec_http2 = True,
    omit_io_netty_codec_socks = True,
    omit_io_netty_common = True,
    omit_io_netty_handler = True,
    omit_io_netty_handler_proxy = True,
    omit_io_netty_resolver = True,
    omit_io_netty_tcnative_boringssl_static = True,
    omit_io_netty_transport = True,
    omit_io_opencensus_api = True,
    omit_io_opencensus_grpc_metrics = True,
    omit_junit_junit = True,
    omit_org_apache_commons_lang3 = True,
    omit_com_google_protobuf_javalite = True
)

# Google Maven Repository
GMAVEN_TAG = "20180513-1"

http_archive(
    name = "gmaven_rules",
    strip_prefix = "gmaven_rules-%s" % GMAVEN_TAG,
    url = "https://github.com/bazelbuild/gmaven_rules/archive/%s.tar.gz" % GMAVEN_TAG,
)

load("@gmaven_rules//:gmaven.bzl", "gmaven_rules")

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
    path = "/home/valerii/Android/Sdk",
    api_level = 27,
    build_tools_version = "27.0.3"
)

http_archive(
    name = "startup_os",
    urls = ["https://github.com/google/startup-os/archive/282be331b50b31f9d6c0d995a27575bba1c703c8.zip"],
    strip_prefix = "startup-os-282be331b50b31f9d6c0d995a27575bba1c703c8",
)

# XXX Use maven deps
maven_jar(
  name = "com_squareup_okhttp_okhttp_2_7_2",
  artifact = "com.squareup.okhttp:okhttp:jar:2.7.2",
)

maven_jar(
  name = "com_squareup_okio_okio_1_6_0",
  artifact = "com.squareup.okio:okio:jar:1.6.0",
)

http_archive(
    name = "com_google_protobuf",
    urls = ["https://github.com/google/protobuf/archive/3.5.1.1.zip"],
    strip_prefix = "protobuf-3.5.1.1",
)

#http_archive(
#    name = "com_google_protobuf_javalite",
#    strip_prefix = "protobuf-javalite",
#    urls = ["https://github.com/google/protobuf/archive/javalite.zip"],
#)

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

