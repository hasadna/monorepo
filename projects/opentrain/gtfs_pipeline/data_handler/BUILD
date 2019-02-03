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
    deps = ["//projects/opentrain/gtfs_pipeline/common:java_agencyproto", "//projects/opentrain/gtfs_pipeline/common:java_calendarproto", "//projects/opentrain/gtfs_pipeline/common:java_fareattributesproto", "//projects/opentrain/gtfs_pipeline/common:java_farerulesproto", "//projects/opentrain/gtfs_pipeline/common:java_routesproto", "//projects/opentrain/gtfs_pipeline/common:java_shapesproto", "//projects/opentrain/gtfs_pipeline/common:java_stoptimesproto", "//projects/opentrain/gtfs_pipeline/common:java_stopsproto",  "//third_party/maven/com/google/protobuf:protobuf_java", "//third_party/maven/org/apache/commons:commons_csv"],
    main_class = "projects.opentrain.gtfs_pipeline.data_handler.DataHandler",
    visibility = ["//visibility:public"],
)