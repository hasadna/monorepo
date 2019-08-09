package(default_visibility = ["//visibility:public"])

load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

checkstyle_test(
    name = "israel_railways_pipeline-checkstyle",
    allow_failure = 1,
    config = "//tools/checkstyle:config.xml",
    target = ":israel_railways_pipeline",
)

proto_library(
    name = "test_proto",
    srcs = ["test_proto.proto"],
)

java_proto_library(
    name = "test_java_proto",
    deps = [":test_proto"],
)

java_binary(
    name = "israel_railways_pipeline",
    srcs = ["DataHandler.java"],
    main_class = "projects.opentrain.israel_railways_pipeline.data_handler.DataHandler",
    deps = [
        # "  //projects/opentrain/gtfs_pipeline/common:common_java_proto",
        #"//projects/opentrain/israel_railways_pipeline/common:common_java_proto",
        #"//projects/opentrain/israel_railways_pipeline/common:common_proto",
        "//third_party/maven/com/google/guava",
        #  "//third_party/maven/com/google/protobuf:protobuf_java",
        "//third_party/maven/org/apache/commons:commons_csv",
        "//third_party/maven/com/google/flogger:flogger_system_backend",
        #"//projects/opentrain/israel_railways_pipeline/test_proto:test_proto_java_proto",
        #"//projects/opentrain/israel_railways_pipeline/test_proto:test_proto_java_proto",
        ":test_java_proto",
    ],
)

#genrule(
#   name = "open_train",
#  srcs = [
#     "@israel_railways_data//:routes.csv",
#    "@israel_railways_data//:samples.csv",
#    "@israel_railways_data//:stops.csv",
#    "@israel_railways_data//:trips.csv",
#  ],
# outs = [
#     "routes.protobin",
#     "samples.protobin",
#    "stops.protobin",
#        "trips.protobin",
#    ],
#    cmd = "$(location //projects/opentrain/israel_railways_pipeline:israel_railways_pipeline) $(location @israel_railways_data//:routes.csv) $(location @israel_railways_data//:samples.csv) $(location @israel_railways_data//:stops.csv) $(location @israel_railways_data//:trips.csv)  $(location routes.protobin) $(location samples.protobin) $(location stops.protobin) $(location trips.protobin)$@",
#    tools = ["//projects/opentrain/israel_railways_pipeline"],
#    visibility = ["//visibility:public"],
#)
