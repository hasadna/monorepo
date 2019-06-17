load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

java_binary(
    name = "Data_Uploader",
    srcs = ["DataUploader.java"],
    data = ["spam.prototxt"],
    main_class = "hasadna.projects.noloan.server.DataUploader",
    deps = [
        "//projects/noloan/app/src/main/proto:SMS_java_proto",
        "//third_party/maven/com/google/flogger:flogger_system_backend",
        "//third_party/maven/javax/inject:javax_inject",
        "@startup_os//common",
        "@startup_os//common:dagger_with_annotation_processor",
        "@startup_os//common/firestore",
        "@startup_os//common/flags",
        "@startup_os//tools/reviewer/local_server/service:auth_service",
        "//third_party/maven/com/google/http_client:google_http_client_jackson"
    ],
)

checkstyle_test(
    name = "Data_Uploader-checkstyle",
    config = "//tools/checkstyle:config.xml",
    target = ":Data_Uploader",
)
