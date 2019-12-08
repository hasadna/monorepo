# THIS FILE IS AUTO-GENERATED

package(default_visibility = ["//visibility:public"])

load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

java_binary(
    name = "data_handler",
    srcs = ["DataHandler.java"],
    main_class = "projects.opentrain.gtfs_pipeline.data_handler.DataHandler",
    deps = [
        "//projects/opentrain/gtfs_pipeline/common:common_java_proto",
        "//third_party/maven/com/google/flogger:flogger_system_backend",
        "//third_party/maven/org/apache/commons:commons_csv",
    ],
)

checkstyle_test(
    name = "data_handler-checkstyle",
    config = "//tools/checkstyle:config.xml",
    target = ":data_handler",
)
