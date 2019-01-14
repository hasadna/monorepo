package(default_visibility = ["//visibility:public"])

load("//third_party:java_grpc_library.bzl", "java_grpc_library")
load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

proto_library(
    name = "service_proto",
    srcs = ["service.proto"],
)

java_proto_library(
    name = "service_java_proto",
    deps = [":service_proto"],
)

java_grpc_library(
    name = "service_java_grpc",
    srcs = [":service_proto"],
    deps = [
        ":service_java_proto",
    ],
    tags = ["checkstyle_ignore"]
)

java_library(
    name = "service",
    srcs = [
        "StorytellerService.java",
    ],
    deps = [
        "service_java_grpc",
        "service_java_proto",
        "//third_party/maven/com/google/flogger",
        "//third_party/maven/com/google/flogger:flogger_system_backend",
        "//third_party/maven/io/grpc:grpc_core",
        "//third_party/maven/io/grpc:grpc_stub",
        "//third_party/maven/io/opencensus:opencensus_contrib_grpc_metrics",
        "//third_party/maven/javax/inject:javax_inject",
        "@startup_os//common/firestore",
        "@startup_os//common/flags",
        "@startup_os//tools/reviewer/local_server/service:auth_service",
    ],
)

checkstyle_test(
    name = "service-checkstyle",
    target = ":service",
    allow_failure = 1,
    config = "//tools/checkstyle:config.xml",
)

java_binary(
    name = "test_tool",
    srcs = [
        "TestTool.java",
    ],
    main_class = "tools.storyteller.service.TestTool",
    deps = [
        "service_java_grpc",
        "service_java_proto",
        ":service",
        "//third_party/maven/io/grpc:grpc_core",
        "//third_party/maven/io/grpc:grpc_netty",
        "//third_party/maven/org/slf4j:slf4j_simple",
        "@startup_os//common",
    ],
)

checkstyle_test(
    name = "test_tool-checkstyle",
    target = ":test_tool",
    allow_failure = 1,
    config = "//tools/checkstyle:config.xml",
)
