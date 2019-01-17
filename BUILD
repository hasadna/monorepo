# gtfs_pipeline definition

genrule(
    name = "data_analysis_part1",
    srcs = ["@gtfs_data//:agency.txt", "@gtfs_data//:calendar.txt", "@gtfs_data//:fare_attributes.txt", "@gtfs_data//:fare_rules.txt", "@gtfs_data//:routes.txt",
    "@gtfs_data//:shapes.txt", "@gtfs_data//:stop_times.txt", "@gtfs_data//:stops.txt", "@gtfs_data//:translations.txt", "@gtfs_data//:trips.txt"],
    cmd = "$(location //projects/opentrain/gtfs_pipeline/handle_data:handle_data) $(location @gtfs_data//:agency.txt) $(location @gtfs_data//:calendar.txt) $@",
    outs = ["fileName_numberLines"],
    tools = ["//projects/opentrain/gtfs_pipeline/handle_data:handle_data"],
)

genrule(
    name = "step1",
    srcs = ["@step1_data//:file1.txt", "@step1_data//:file2.txt"],
    cmd = "$(location //projects/opentrain/gtfs_pipeline/task1:task1) $(location @step1_data//:file1.txt) $(location @step1_data//:file2.txt) $@",
    outs = ["merged_file1_file2.prototxt"],
    tools = ["//projects/opentrain/gtfs_pipeline/task1:task1"],
)

# Uses output of step 1
genrule(
    name = "step2",
    srcs = [":step1"],
    cmd = "$(location //projects/opentrain/gtfs_pipeline/task2:task2) $(location :step1) $@",
    outs = ["all_users.count"],
    tools = ["//projects/opentrain/gtfs_pipeline/task2:task2"],
)

# Uses pre-downloaded output of step 1
genrule(
    name = "step2_prebuilt",
    srcs = ["@step1_prebuilt_output//file:file"],
    cmd = "$(location //projects/opentrain/gtfs_pipeline/task2:task2) $(location @step1_prebuilt_output//file:file) $@",
    outs = ["all_users_prebuilt.count"],
    tools = ["//projects/opentrain/gtfs_pipeline/task2:task2"],
)

genrule(
    name = "step2_prebuilt_from_archive",
    srcs = ["@step1_prebuilt_zipped_output//:merged_file1_file2.prototxt"],
    cmd = "$(location //projects/opentrain/gtfs_pipeline/task2:task2) $(location @step1_prebuilt_zipped_output//:merged_file1_file2.prototxt) $@",
    outs = ["all_users_prebuilt_archive.count"],
    tools = ["//projects/opentrain/gtfs_pipeline/task2:task2"],
)

