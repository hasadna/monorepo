# THIS FILE IS AUTO-GENERATED

package(default_visibility = ["//visibility:public"])

load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

java_binary(
    name = "task1",
    srcs = ["Task1.java"],
    main_class = "projects.data_analysis.task1.Task1",
    deps = [
        "//projects/data_analysis/common:messages_java_proto",
        "//third_party/maven/com/google/flogger:flogger_system_backend",
        "//third_party/maven/com/google/protobuf:protobuf_java",
    ],
)

checkstyle_test(
    name = "task1-checkstyle",
    config = "//tools/checkstyle:config.xml",
    target = ":task1",
)
