package(default_visibility = ["//visibility:public"])
load("@io_grpc_grpc_java//:java_grpc_library.bzl", "java_grpc_library")

java_binary(
    name = "local_server",
    srcs = [
        "LocalServer.java",
    ],
    main_class = "tools.local_server.LocalServer",
    deps = [
        "@startup_os//common",
        "@startup_os//common:dagger_with_annotation_processor",
        "//third_party/maven/io/grpc:grpc_core",
        "//third_party/maven/io/grpc:grpc_netty",
        "//third_party/maven/io/opencensus:opencensus_contrib_grpc_metrics",
        "//third_party/maven/javax/inject:javax_inject",
        "@startup_os//tools/local_server/service:auth_service",
        "@startup_os//tools/reviewer/service:service",
        "//tools/storyteller/service",
        "//third_party/maven/com/google/flogger:flogger",
        "//third_party/maven/com/google/flogger:flogger_system_backend",
        "@startup_os//common/flags",
        "//third_party/maven/org/slf4j:slf4j_simple",
    ],
)
