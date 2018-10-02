package(default_visibility = ["//visibility:public"])

load("//third_party:java_grpc_library.bzl", "java_grpc_library")

java_binary(
    name = "local_server",
    srcs = [
        "LocalServer.java",
    ],
    main_class = "tools.local_server.LocalServer",
    deps = [
        "//third_party/maven/com/google/flogger",
        "//third_party/maven/com/google/flogger:flogger_system_backend",
        "//third_party/maven/io/grpc:grpc_core",
        "//third_party/maven/io/grpc:grpc_netty",
        "//third_party/maven/io/opencensus:opencensus_contrib_grpc_metrics",
        "//third_party/maven/javax/inject:javax_inject",
        "//third_party/maven/org/slf4j:slf4j_simple",
        "//tools/storyteller/service",
        "@startup_os//common",
        "@startup_os//common:dagger_with_annotation_processor",
        "@startup_os//common/flags",
        "@startup_os//tools/reviewer/local_server/service:auth_service",
        "@startup_os//tools/reviewer/local_server/service:code_review_service",
    ],
)
