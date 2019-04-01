# Pipeline definition

genrule(
    name = "step1",
    srcs = ["@step1_data//:file1.txt", "@step1_data//:file2.txt"],
    cmd = "$(location //projects/data_analysis/task1:task1) $(location @step1_data//:file1.txt) $(location @step1_data//:file2.txt) $@",
    outs = ["merged_file1_file2.prototxt"],
    tools = ["//projects/data_analysis/task1:task1"],
)

# Uses output of step 1
genrule(
    name = "step2",
    srcs = [":step1"],
    cmd = "$(location //projects/data_analysis/task2:task2) $(location :step1) $@",
    outs = ["all_users.count"],
    tools = ["//projects/data_analysis/task2:task2"],
)

# Uses pre-downloaded output of step 1
genrule(
    name = "step2_prebuilt",
    srcs = ["@step1_prebuilt_output//file:file"],
    cmd = "$(location //projects/data_analysis/task2:task2) $(location @step1_prebuilt_output//file:file) $@",
    outs = ["all_users_prebuilt.count"],
    tools = ["//projects/data_analysis/task2:task2"],
)

genrule(
    name = "step2_prebuilt_from_archive",
    srcs = ["@step1_prebuilt_zipped_output//:merged_file1_file2.prototxt"],
    cmd = "$(location //projects/data_analysis/task2:task2) $(location @step1_prebuilt_zipped_output//:merged_file1_file2.prototxt) $@",
    outs = ["all_users_prebuilt_archive.count"],
    tools = ["//projects/data_analysis/task2:task2"],
)
