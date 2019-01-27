load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

java_binary(
    name = "task1",
    srcs = ["Task1.java"],
    deps = ["//projects/data_analysis/common:java_common", "//third_party/maven/com/google/protobuf:protobuf_java"],
    main_class = "com.example.tasks.Task1",
    visibility = ["//projects/data_analysis:__pkg__"]
)

checkstyle_test(
    name = "task1-checkstyle",
    target = ":task1",
    config = "//tools/checkstyle:config.xml",
)
