load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

checkstyle_test(
    name = "linecount-checkstyle",
    target = ":linecount",
    allow_failure = 1,
    config = "//tools/checkstyle:config.xml",
)

java_binary(
    name = "linecount",
    srcs = ["LineCount.java"],
    deps = ["//projects/opentrain/gtfs_pipeline/common:common_java_proto",
            "//third_party/maven/com/google/protobuf:protobuf_java",
            "//third_party/maven/org/apache/commons:commons_csv",
            "//third_party/maven/com/google/guava"],
    main_class = "projects.data_analysis.linecount.LineCount",
    visibility = ["//visibility:public"],
)