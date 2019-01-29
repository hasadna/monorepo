load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

proto_library(
    name = "SMS_proto",
    srcs = ["//projects/noloan2/app/src/main/proto/SMS.proto"],
)

java_binary(
    name = "Data_Uploader",
    srcs = ["DataUploader.java"],
    data = ["spam.prototxt"],
    main_class = "hasadna.projects.noloan2.server.DataUploader",
    deps = [
        ":SMS_proto",
        "//third_party/maven/com/google/flogger:flogger_system_backend",
        "//third_party/maven/javax/inject:javax_inject",
        "@startup_os//common",
        "@startup_os//common:dagger_with_annotation_processor",
        "@startup_os//common/flags",
    ],
)

checkstyle_test(
    name = "Data_Uploader-checkstyle",
    config = "//tools/checkstyle:config.xml",
    target = ":Data_Uploader",
)
