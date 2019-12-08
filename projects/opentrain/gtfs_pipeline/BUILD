# gtfs_pipeline definition

genrule(
    name = "data_analysis_part1",
    srcs = ["@gtfs_data//:calendar.txt",
            "@gtfs_data//:routes.txt",
            "@gtfs_data//:stop_times.txt",
            "@gtfs_data//:stops.txt",
            "@gtfs_data//:translations.txt",
            "@gtfs_data//:trips.txt"
            ],
    cmd = "$(location //projects/opentrain/gtfs_pipeline/data_handler:data_handler) $(location @gtfs_data//:calendar.txt) $(location @gtfs_data//:routes.txt) $(location @gtfs_data//:stop_times.txt) $(location @gtfs_data//:stops.txt) $(location @gtfs_data//:translations.txt) $(location @gtfs_data//:trips.txt) $(location calendar.protobin) $(location route.protobin) $(location stopTime.protobin) $(location stops.protobin) $(location translatations.protobin) $(location trips.protobin)",
    outs = ["calendar.protobin", "route.protobin", "stopTime.protobin", "stops.protobin", "translatations.protobin", "trips.protobin"],
    tools = ["//projects/opentrain/gtfs_pipeline/data_handler:data_handler"],
	visibility = ["//visibility:public"],
)
