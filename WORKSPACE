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
    name = "android_firestore_sample",
    urls = ["https://github.com/google/startup-os/archive/30bdc6aa23a24a9514d26e9bd2023e9e46ce8c64.zip"],
    strip_prefix = "startup-os-30bdc6aa23a24a9514d26e9bd2023e9e46ce8c64"
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
