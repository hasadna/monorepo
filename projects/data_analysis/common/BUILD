proto_library(
    name = "common",
    srcs = ["messages.proto"]
)

java_proto_library(
    name = "java_common",
    deps = [":common"],
    visibility = ["//visibility:public"]
)
