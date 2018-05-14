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
        "StoryVerifier.java",
        "StoryWriter.java",
        "Storyteller.java",
        "StorytellerComponent.java",
        "StorytellerConfig.java",
    ],
    deps = [
        ":invoicer",
        ":storyteller_java_proto",
        "//third_party/maven/com/google/guava",
        "//third_party/maven/com/google/protobuf:protobuf_java",
        "//third_party/maven/javax/inject:javax_inject",
        "//tools/storyteller/service:service_lib",
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
        "//third_party/maven/javax/inject:javax_inject",
        "//third_party/maven/org/apache/pdfbox",
        "//third_party/maven/org/apache/pdfbox:fontbox",
        "//tools/storyteller/service:service_lib",
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
        "//third_party/maven/com/google/guava",
        "//third_party/maven/com/google/protobuf:protobuf_java",
        "//third_party/maven/javax/inject:javax_inject",
        "@startup_os//common",
        "@startup_os//common:dagger_with_annotation_processor",
        "@startup_os//common/flags",
    ],
)

STORYTELLER_UI_TOOL_DEPS = [
    ":storyteller",
    ":storyteller_java_proto",
    "//tools/storyteller/service:service_lib",
    "//third_party/maven/com/google/guava:guava",
    "//third_party/maven/com/google/protobuf:protobuf_java",
    "@startup_os//common",
    "@startup_os//common/flags",
    "//third_party/maven/javax/inject:javax_inject",
    "@startup_os//common:dagger_with_annotation_processor",
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
