proto_library(
    name = "data_protos",
    srcs = ["data.proto"],
)


proto_library(
	name = "bayes_protos",
	srcs = ["bayes.proto"]
)


java_lite_proto_library(
    name = "data_protos_java",
    visibility = ["//visibility:public"],
    deps = [":data_protos"]
)


java_lite_proto_library(
    name = "bayes_protos_java",
    visibility = ["//visibility:public"],
    deps = [":bayes_protos"]
)
