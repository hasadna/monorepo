load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

checkstyle_test(
    name = "stackoverflow-checkstyle",
    target = ":stackoverflow",
    allow_failure = 1,
    config = "//tools/checkstyle:config.xml",
)

java_binary(
    name = "stackoverflow",
    srcs = ["DataExtractor.java",
        "ExtractedRow.java" ],
    main_class = "projects.stackoverflow.DataExtractor",
    visibility = ["//visibility:public"],
)
