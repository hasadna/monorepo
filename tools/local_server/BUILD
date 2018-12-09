package(default_visibility = ["//visibility:public"])

load("//third_party:java_grpc_library.bzl", "java_grpc_library")

java_binary(
    name = "local_server",
    srcs = [
        "LocalServer.java",
	"Lists.java",
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

java_library(
    name = "lists",
    srcs = ["Lists.java"],
    deps = [
        "//third_party/maven/com/google/auto/value:auto_value",
        "//third_party/maven/com/google/code/findbugs:jsr305",
        "//third_party/maven/com/google/guava",
    ],
)

java_library(
    name = "text_differencer",
    srcs = [
        "TextDifferencer.java",
    ],
    deps = [
        ":diff_match_patch",
        ":lists",
        ":text_difference_java_proto",
        "//common/repo:repo_java_proto",
        "//third_party/maven/com/google/guava",
        "//third_party/maven/javax/inject:javax_inject",
    ],
)

