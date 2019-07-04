# THIS FILE IS AUTO-GENERATED

package(default_visibility = ["//visibility:public"])

load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

java_test(
    name = "foo_test",
    srcs = ["FooTest.java"],
    test_class = "examples.java.simple_test.tests.FooTest",
    deps = [
        "//examples/java/simple_test:foo",
        "//third_party/maven/com/google/flogger:flogger_system_backend",
        "//third_party/maven/junit",
    ],
)

checkstyle_test(
    name = "foo_test-checkstyle",
    config = "//tools/checkstyle:config.xml",
    target = ":foo_test",
)
