load("@startup_os//tools/checkstyle:checkstyle.bzl", "checkstyle_test")

java_test(
    name = "foo_test",
    srcs = [
        "FooTest.java",
    ],
    test_class = "examples.java.simple_test.tests.FooTest",
    deps = [
        "//third_party/maven/junit",
        "//examples/java/simple_test:foo"
    ],
)

checkstyle_test(
    name = "foo_test-checkstyle",
    target = ":foo_test",
    config = "//tools/checkstyle:config.xml",
)
