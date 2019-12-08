
package(default_visibility = ["//visibility:public"])

load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

java_binary(
    name = "ls-alg_tool",
    srcs = [
        "AvodataSolverTool.java",
    ],
    main_class = "AvodataSolverTool",
    deps = [
        "//third_party/maven/org/apache/commons:commons_math3",
    ],
)

checkstyle_test(
    name = "ls-alg_tool-checkstyle",
    target = ":ls-alg_tool",
    config = "//tools/checkstyle:config.xml",
)