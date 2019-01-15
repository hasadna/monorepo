# Sample data analysis pipeline

This project simulates data analysis workflow with two steps:
* step1: take first names from file1.txt, last names from file2.txt and make a prototxt out of them
* step2: take prototxt from step1 (either from the task, or uploaded result file, or uploaded archive with file) and count number of users 

To run step1:
`bazel build //projects/data_analysis:step1`

That will print out:
```
INFO: Invocation ID: 3d5cdade-758d-4505-b3c7-f4390ad90d32
INFO: Analysed target //projects/data_analysis:step1 (0 packages loaded, 0 targets configured).
INFO: Found 1 target...
Target //projects/data_analysis:step1 up-to-date:
  bazel-genfiles/projects/data_analysis/merged_file1_file2.prototxt
INFO: Elapsed time: 0.171s, Critical Path: 0.00s
INFO: 0 processes.
INFO: Build completed successfully, 1 total action
```

The output is in `bazel-genfiles/projects/data_analysis/merged_file1_file2.prototxt` (it is printed in the output above).

Similarly, you can run:
`bazel build //projects/data_analysis:step2`
`bazel build //projects/data_analysis:step2_prebuilt`
`bazel build //projects/data_analysis:step2_prebuilt_from_archive`
