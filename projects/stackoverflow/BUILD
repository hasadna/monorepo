load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

checkstyle_test(
    name = "data_extractor_tool-checkstyle",
    target = ":data_extractor_tool",
    allow_failure = 1,
    config = "//tools/checkstyle:config.xml",
)

java_binary(
    name = "data_extractor_tool",
    srcs = ["DataExtractorTool.java",
        "ExtractedRow.java" ],
    main_class = "projects.stackoverflow.DataExtractor",
    visibility = ["//visibility:public"],
)
