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
    deps = ["//projects/opentrain/gtfs_pipeline/common:agency_java_proto",
            "//projects/opentrain/gtfs_pipeline/common:calendar_java_proto",
            "//projects/opentrain/gtfs_pipeline/common:fare_attributes_java_proto",
            "//projects/opentrain/gtfs_pipeline/common:fare_rules_java_proto",
            "//projects/opentrain/gtfs_pipeline/common:routes_java_proto",
            "//projects/opentrain/gtfs_pipeline/common:shapes_java_proto",
            "//projects/opentrain/gtfs_pipeline/common:stop_time_java_proto",
            "//projects/opentrain/gtfs_pipeline/common:stops_java_proto",
            "//projects/opentrain/gtfs_pipeline/common:translations_java_proto",
            "//projects/opentrain/gtfs_pipeline/common:trips_java_proto",
            "//third_party/maven/com/google/protobuf:protobuf_java",
            "//third_party/maven/org/apache/commons:commons_csv"],
    main_class = "projects.opentrain.gtfs_pipeline.data_handler.DataHandler",
    visibility = ["//visibility:public"],
)