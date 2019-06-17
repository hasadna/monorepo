# THIS FILE IS AUTO-GENERATED

package(default_visibility = ["//visibility:public"])

load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

java_library(
    name = "foo",
    srcs = ["Foo.java"],
)

checkstyle_test(
    name = "foo-checkstyle",
    config = "//tools/checkstyle:config.xml",
    target = ":foo",
)
