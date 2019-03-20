package(default_visibility = ["//visibility:public"])

load("//third_party:java_grpc_library.bzl", "java_grpc_library")
load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

java_library(
    name = "storyteller",
    srcs = [
        "Storyteller.java",
    ],
    deps = [
        # ":invoicer",
        # "//third_party/maven/com/google/flogger",
        # "//third_party/maven/com/google/flogger:flogger_system_backend",
        # "//third_party/maven/com/google/guava",
        # "//third_party/maven/com/google/protobuf:protobuf_java",
        # "//third_party/maven/io/grpc:grpc_core",
        # "//third_party/maven/io/grpc:grpc_netty",
        "//third_party/maven/javax/inject:javax_inject",
        # "//third_party/maven/org/slf4j:slf4j_simple",
        # "@startup_os//common",
        "@startup_os//common:dagger_with_annotation_processor",
        # "@startup_os//common/firestore",
        # "@startup_os//common/flags",
        # "@startup_os//tools/reviewer/local_server/service:auth_service",
    ],
)

checkstyle_test(
    name = "storyteller-checkstyle",
    target = ":storyteller",
    config = "//tools/checkstyle:config.xml",
)

java_binary(
    name = "storyteller_tool",
    srcs = [
        "StorytellerTool.java",
    ],
    main_class = "tools.storyteller.StorytellerTool",
    deps = [
        ":storyteller",
        # "//third_party/maven/com/google/flogger",
        # "//third_party/maven/com/google/flogger:flogger_system_backend",
        # "//third_party/maven/com/google/guava",
        # "//third_party/maven/com/google/protobuf:protobuf_java",
        # "//third_party/maven/io/netty:netty_tcnative_boringssl_static",
        "//third_party/maven/javax/inject:javax_inject",
        # "//third_party/maven/org/slf4j:slf4j_simple",
        # "@startup_os//common",
        "@startup_os//common:dagger_with_annotation_processor",
        # "@startup_os//common/flags",
        # "@startup_os//tools/reviewer/aa:aa_module",
        # "@startup_os//tools/reviewer/local_server/service:auth_service",
    ],
)

checkstyle_test(
    name = "storyteller_tool-checkstyle",
    target = ":storyteller_tool",
    config = "//tools/checkstyle:config.xml",
)
