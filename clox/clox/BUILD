cc_library(
    name = "clox",
    srcs = [
        "src/chunk.c",
        "src/debug.c",
        "src/memory.c",
        "src/value.c",
    ],
    hdrs = [
        "include/clox/chunk.h",
        "include/clox/common.h",
        "include/clox/debug.h",
        "include/clox/memory.h",
        "include/clox/value.h",
    ],
    includes = [
        "include",
    ],
)

cc_binary(
    name = "main",
    srcs = [
        "main/main.c",
    ],
    deps = [":clox"],
)

cc_test(
    name = "test",
    srcs = [
        "test/chunk.cc",
        "test/value.cc",
    ],
    deps = [
        ":clox",
        "@gtest//:gtest_main",
    ],
)
