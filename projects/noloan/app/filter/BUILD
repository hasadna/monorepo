package(default_visibility = ["//projects/noloan/app:__subpackages__"])

load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")


java_library(
    name = "bayes_spam_filter",
    srcs = glob(["bayes/*.java"]),
    deps = [
        "//projects/noloan/app/proto:bayes_protos_java",
    ],
)

checkstyle_test(
    name = "bayes_spam_filter-checkstyle",
    target = ":bayes_spam_filter",
    config = "//tools/checkstyle:config.xml",
)

java_library(
    name = "spam_filter",
    srcs = ["SpamFilter.java"],
    resources = ["spam.prototxt"],
    deps =
        [
            ":bayes_spam_filter",
            "//projects/noloan/app/proto:bayes_protos_java",
            "//projects/noloan/app/proto:data_protos_java",
            "//third_party/maven/com/google/guava",
            "//third_party/maven/com/google/protobuf:protobuf_java",
        ],
)

checkstyle_test(
    name = "spam_filter-checkstyle",
    target = ":spam_filter",
    config = "//tools/checkstyle:config.xml",
)

java_binary(
    name = "spam_filter_tool",
    srcs = ["SpamFilterTool.java"],
    main_class = "projects.noloan.app.filter.SpamFilterTool",
    deps = [
        ":spam_filter",
        "//projects/noloan/app/proto:data_protos_java",
        "//third_party/maven/com/google/flogger",
        "//third_party/maven/com/google/flogger:flogger_system_backend",
        "//third_party/maven/com/google/guava",
        "//third_party/maven/javax/inject:javax_inject",
        "//third_party/maven/org/apache/commons:commons_csv",
        "//third_party/maven/org/slf4j:slf4j_simple",
        "@startup_os//common",
        "@startup_os//common:dagger_with_annotation_processor",
        "@startup_os//common/flags",
    ],
)

checkstyle_test(
    name = "spam_filter_tool-checkstyle",
    target = ":spam_filter_tool",
    config = "//tools/checkstyle:config.xml",
)
