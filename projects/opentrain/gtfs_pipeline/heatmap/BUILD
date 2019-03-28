load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

java_binary(
    name = "heat_map",
    srcs = ["HeatMap.java"],
    deps = [
        "//projects/opentrain/gtfs_pipeline/common:common_java_proto",
        "//third_party/maven/com/google/protobuf:protobuf_java",
    ],
    main_class = "projects.opentrain.gtfs_pipeline.heatmap.HeatMap",
    data = [
        "//projects/opentrain/gtfs_pipeline:data_analysis_part1",
    ],
    visibility = ["//visibility:public"],
)

checkstyle_test(
    name = "heat_map-checkstyle",
    target = ":heat_map",
    allow_failure = 1,
    config = "//tools/checkstyle:config.xml",
)
