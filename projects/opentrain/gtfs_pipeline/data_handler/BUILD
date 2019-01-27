load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

checkstyle_test(
    name = "data_handler-checkstyle",
    target = ":data_handler",
    allow_failure = 1,
    config = "//tools/checkstyle:config.xml",
)

java_binary(
    name = "data_handler",
    srcs = ["DataHandler.java"],
    main_class = "projects.opentrain.gtfs_pipeline.data_handler.DataHandler",
    visibility = ["//visibility:public"],
)