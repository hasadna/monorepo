load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

java_binary(
    name = "task2",
    srcs = ["Main.java"],
    deps = ["//projects/data_analysis/common:java_common", "//third_party/maven/com/google/protobuf:protobuf_java"],
    main_class = "com.example.tasks.Task2",
    visibility = ["//projects/data_analysis:__pkg__"]
)

checkstyle_test(
    name = "task2-checkstyle",
    target = ":task2",
    allow_failure = 1,
    config = "//tools/checkstyle:config.xml",
)
