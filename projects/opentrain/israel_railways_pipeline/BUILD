load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

checkstyle_test(
    name = "israel_railways_pipeline-checkstyle",
    allow_failure = 1,
    config = "//tools/checkstyle:config.xml",
    target = ":israel_railways_pipeline",
)

java_binary(
    name = "israel_railways_pipeline",
    srcs = ["datahandler/DataHandler.java"],
    main_class = "projects.opentrain.israel_railways_pipeline.datahandler.DataHandler",
    visibility = ["//visibility:public"],
    deps = [
        "//projects/opentrain/israel_railways_pipeline/common:railWay_proto",
        "//third_party/maven/com/google/guava",
        "//third_party/maven/com/google/protobuf:protobuf_java",
        "//third_party/maven/org/apache/commons:commons_csv",
    ],
)

#genrule(
 #   name = "open_train",
  #  srcs = [
   #     "@israel_railways_data//:routes.csv",
    #    "@israel_railways_data//:samples.csv",
     #   "@israel_railways_data//:stops.csv",
      #  "@israel_railways_data//:trips.csv",
   # ],
    #outs = [
     #   "routes.protobin",
      #  "samples.protobin",
       # "stops.protobin",
        #"trips.protobin",
   # ],
    #cmd = "$(location //projects/opentrain/israel_railways_pipeline:israel_railways_pipeline) $(location @israel_railways_data//:routes.csv) $(location routes.protobin) $(location @israel_railways_data//:samples.csv) $(location samples.protobin) $(location @israel_railways_data//:stops.csv) $(location stops.protobin) $(location @israel_railways_data//:trips.csv) $(location trips.protobin) ",
   # tools = ["//projects/opentrain/israel_railways_pipeline"],
#)
