package(default_visibility = ["//visibility:public"])

load("//third_party:java_grpc_library.bzl", "java_grpc_library")

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
        "//third_party/maven/org/slf4j:slf4j_api",
        "//third_party/maven/org/slf4j:slf4j_simple",
        "//tools/storyteller/service:service_java_grpc",
        "//tools/storyteller/service:service_java_proto",
        "@startup_os//common",
        "@startup_os//common:dagger_with_annotation_processor",
        "@startup_os//common/flags",
    ],
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
        "//tools/storyteller/service:service_java_grpc",
        "//tools/storyteller/service:service_java_proto",
        "@startup_os//common",
        "@startup_os//common:dagger_with_annotation_processor",
        "@startup_os//common/flags",
    ],
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
        "//third_party/maven/javax/inject:javax_inject",
        "//third_party/maven/org/slf4j:slf4j_api",
        "//third_party/maven/org/slf4j:slf4j_simple",
        "@startup_os//common",
        "@startup_os//common:dagger_with_annotation_processor",
        "@startup_os//common/flags",
    ],
)

STORYTELLER_UI_TOOL_DEPS = [
    ":storyteller",
    ":storyteller_java_proto",
    "//tools/storyteller/service:service_proto",
    "//third_party/maven/com/google/guava:guava",
    "//third_party/maven/com/google/protobuf:protobuf_java",
    "@startup_os//common",
    "@startup_os//common/flags",
    "//third_party/maven/javax/inject:javax_inject",
    "@startup_os//common:dagger_with_annotation_processor",
    "//tools/storyteller/service:service_java_grpc",
    "//tools/storyteller/service:service_java_proto",
    "//third_party/maven/io/grpc:grpc_core",
    "//third_party/maven/io/grpc:grpc_netty",
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
