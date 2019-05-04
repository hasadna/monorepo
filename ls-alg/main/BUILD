
package(default_visibility = ["//visibility:public"])

load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

java_binary(
    name = "ls-alg_tool",
    srcs = [
        "Main.java",
    ],
    main_class = "main.Main",
    deps = [
        "//third_party/maven/org/apache/commons:commons_math3",
    ],
)

checkstyle_test(
    name = "ls-alg_tool-checkstyle",
    target = ":ls-alg_tool",
    config = "//tools/checkstyle:config.xml",
)