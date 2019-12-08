package(default_visibility = ["//visibility:public"])

load("//third_party:java_grpc_library.bzl", "java_grpc_library")
load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

proto_library(
    name = "storyteller_proto",
    srcs = ["data.proto"],
)

java_proto_library(
    name = "storyteller_java_proto",
    deps = [":storyteller_proto"],
)

java_library(
    name = "storyteller",
    srcs = [
        "ArrowButton.java",
        "StoryReader.java",
        "StoryWriter.java",
        "Storyteller.java",
        "StorytellerConfig.java",
    ],
    deps = [
        ":invoicer",
        ":storyteller_java_proto",
        "//third_party/maven/com/google/flogger",
        "//third_party/maven/com/google/flogger:flogger_system_backend",
        "//third_party/maven/com/google/guava",
        "//third_party/maven/com/google/protobuf:protobuf_java",
        "//third_party/maven/io/grpc:grpc_core",
        "//third_party/maven/io/grpc:grpc_netty",
        "//third_party/maven/javax/inject:javax_inject",
        "//third_party/maven/org/slf4j:slf4j_simple",
        "@startup_os//common",
        "@startup_os//common:dagger_with_annotation_processor",
        "@startup_os//common/firestore",
        "@startup_os//common/flags",
        "@startup_os//tools/reviewer/local_server/service:auth_service",
    ],
)

checkstyle_test(
    name = "storyteller-checkstyle",
    target = ":storyteller",
    config = "//tools/checkstyle:config.xml",
)

java_library(
    name = "invoicer",
    srcs = [
        "Invoicer.java",
    ],
    deps = [
        ":storyteller_java_proto",
        "//third_party/maven/com/google/guava",
        "//third_party/maven/com/google/protobuf:protobuf_java",
        "//third_party/maven/commons_logging",
        "//third_party/maven/io/grpc:grpc_core",
        "//third_party/maven/io/grpc:grpc_netty",
        "//third_party/maven/javax/inject:javax_inject",
        "//third_party/maven/org/apache/pdfbox",
        "//third_party/maven/org/apache/pdfbox:fontbox",
        "@startup_os//common",
        "@startup_os//common:dagger_with_annotation_processor",
        "@startup_os//common/flags",
    ],
)

checkstyle_test(
    name = "invoicer-checkstyle",
    target = ":invoicer",
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
        ":storyteller_java_proto",
        "//third_party/maven/com/google/flogger",
        "//third_party/maven/com/google/flogger:flogger_system_backend",
        "//third_party/maven/com/google/guava",
        "//third_party/maven/com/google/protobuf:protobuf_java",
        "//third_party/maven/io/netty:netty_tcnative_boringssl_static",
        "//third_party/maven/javax/inject:javax_inject",
        "//third_party/maven/org/slf4j:slf4j_simple",
        "@startup_os//common",
        "@startup_os//common:dagger_with_annotation_processor",
        "@startup_os//common/flags",
        "@startup_os//tools/reviewer/aa:aa_module",
        "@startup_os//tools/reviewer/local_server/service:auth_service",
    ],
)

checkstyle_test(
    name = "storyteller_tool-checkstyle",
    target = ":storyteller_tool",
    config = "//tools/checkstyle:config.xml",
)

STORYTELLER_UI_TOOL_DEPS = [
    ":storyteller",
    ":storyteller_java_proto",
    "//third_party/maven/com/google/guava:guava",
    "//third_party/maven/com/google/protobuf:protobuf_java",
    "@startup_os//common",
    "@startup_os//common/flags",
    "@startup_os//tools/reviewer/aa:aa_module",
    "//third_party/maven/javax/inject:javax_inject",
    "@startup_os//common:dagger_with_annotation_processor",
    "@startup_os//tools/reviewer/local_server/service:auth_service",
    "//third_party/maven/io/grpc:grpc_core",
    "//third_party/maven/io/grpc:grpc_netty",
    "//third_party/maven/io/netty:netty_tcnative_boringssl_static",
]

java_binary(
    name = "storyteller_time_and_screenshot_ui_tool",
    srcs = [
        "StorytellerUiTool.java",
    ],
    args = ["time_and_screenshot"],
    main_class = "tools.storyteller.StorytellerUiTool",
    resources = glob(["res/*"]),
    deps = STORYTELLER_UI_TOOL_DEPS,
)

checkstyle_test(
    name = "storyteller_time_and_screenshot_ui_tool-checkstyle",
    target = ":storyteller_time_and_screenshot_ui_tool",
    config = "//tools/checkstyle:config.xml",
)

java_binary(
    name = "storyteller_screenshot_ui_tool",
    srcs = [
        "StorytellerUiTool.java",
    ],
    args = ["screenshot_only"],
    main_class = "tools.storyteller.StorytellerUiTool",
    resources = glob(["res/*"]),
    deps = STORYTELLER_UI_TOOL_DEPS,
)

checkstyle_test(
    name = "storyteller_screenshot_ui_tool-checkstyle",
    target = ":storyteller_screenshot_ui_tool",
    config = "//tools/checkstyle:config.xml",
)