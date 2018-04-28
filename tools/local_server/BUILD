package(default_visibility = ["//visibility:public"])

java_binary(
    name = "local_server",
    srcs = [
        "LocalServer.java",
    ],
    main_class = "tools.localserver.LocalServer",
    deps = [
        "@startup_os//tools/reviewer/service",
        "@startup_os//tools/reviewer/service:service_lib",
        "@startup_os//tools/local_server/service:auth_service",
        "//tools/storyteller/service",
        "//third_party/maven/io/opencensus:opencensus_contrib_grpc_metrics",
        "@org_pubref_rules_protobuf//java:netty_runtime_deps",
        "@startup_os//common/flags",
        "//third_party/maven/org/slf4j:slf4j_simple",
    ],
)
