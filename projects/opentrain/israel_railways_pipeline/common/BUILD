package(default_visibility = ["//visibility:public"])

proto_library(
    name = "common_proto",
    srcs = ["common.proto"],
)

java_proto_library(
    name = "common_java_proto",
    visibility = ["//visibility:public"],
    deps = [":common_proto"],
)
