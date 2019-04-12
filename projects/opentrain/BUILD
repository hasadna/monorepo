genrule(
    name = "open_train",
    srcs = [
        "@israel_railways_data//:routes.csv",
        "@israel_railways_data//:samples.csv",
        "@israel_railways_data//:stops.csv",
        "@israel_railways_data//:trips.csv",
    ],
    outs = ["output.txt"],
    cmd = "$(location //projects/OpenTrain/israel_railways_pipeline/data_handler:israel_railways_pipeline) $(location @israel_railways_data//:routes.csv) $(location @israel_railways_data//:samples.csv) $(location @israel_railways_data//:stops.csv) $(location @israel_railways_data//:trips.csv) $@",
    tools = ["//projects/OpenTrain/israel_railways_pipeline/data_handler:israel_railways_pipeline"],
)
