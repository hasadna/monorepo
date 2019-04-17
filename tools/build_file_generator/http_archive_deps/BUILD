package(default_visibility = ["//visibility:public"])

load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

java_binary(
    name = "http_archive_deps_tool",
    srcs = ["HttpArchiveDepsTool.java"],
    main_class = "tools.build_file_generator.http_archive_deps.HttpArchiveDepsTool",
    deps = [
        "//third_party/maven/com/google/flogger:flogger_system_backend",
        "//third_party/maven/javax/inject:javax_inject",
        "@startup_os//common",
        "@startup_os//common:dagger_with_annotation_processor",
        "@startup_os//common/flags",
        "@startup_os//tools/build_file_generator:build_file_generator_java_proto",
        "@startup_os//tools/build_file_generator/http_archive_deps:http_archive_deps_generator",
        "@startup_os//tools/build_file_generator/http_archive_deps:workspace_parser",
    ],
)

checkstyle_test(
    name = "http_archive_deps_tool-checkstyle",
    config = "//tools/checkstyle:config.xml",
    target = ":http_archive_deps_tool",
)
