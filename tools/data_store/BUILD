package(default_visibility = ["//visibility:public"])
load("//third_party:java_grpc_library.bzl", "java_grpc_library")
load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

java_library(
    name = "data_store",
    srcs = [
        "DataStore.java",
    ],
    deps = [
        "//third_party/maven/javax/inject:javax_inject",
        "@startup_os//common:dagger_with_annotation_processor",
    ],
)
checkstyle_test(
    name = "data_store-checkstyle",
    target = ":data_store",
    config = "//tools/checkstyle:config.xml",
)

java_binary(
    name = "data_store_tool",
    srcs = [
        "DataStoreTool.java",
    ],
    main_class = "tools.data_store.DataStoreTool",
    deps = [
        ":data_store",
        "//third_party/maven/javax/inject:javax_inject",
        "@startup_os//common:dagger_with_annotation_processor",

    ],
)
checkstyle_test(
    name = "data_store_tool-checkstyle",
    target = ":data_store_tool",
    config = "//tools/checkstyle:config.xml",
)
