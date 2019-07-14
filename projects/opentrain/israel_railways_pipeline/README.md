# Opentrain pipelines

Project label is `opentrain`.

## Description

Pipelines that help analyse Israel Railways train data. It uses the `data_analysis` pipeline.
* [gtfs_pipeline](gtfs_pipeline) analyses the GTFS data, which contains the planned train schedule.
* [israel_railways_pipeline](israel_railways_pipeline) analyses the Israel Railways data, which contains the actual arrival times of trains and their planned arrival times (but does not include cancelled trains for example).



## How to run gtfs_pipeline

`bazel build //projects/opentrain/gtfs_pipeline:data_analysis_part1`

### Output:
```
INFO: Invocation ID: 0d71b597-9d81-494a-919c-18d3b052350c
INFO: SHA256 (https://firebasestorage.googleapis.com/v0/b/startupos-5f279.appspot.com/o/israel-public-transportation.zip?alt=media&token=a9bc43a5-36a6-4126-9e19-2e42f9ff663c) = 3c44446ffeaea51188a284484553623543d03b94695f3a363131bb3eda86e1c2
INFO: Analysed target //projects/opentrain/gtfs_pipeline:data_analysis_part1 (3 packages loaded, 31 targets configured).
INFO: Found 1 target...
INFO: From Executing genrule //projects/opentrain/gtfs_pipeline:data_analysis_part1:
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by com.google.protobuf.UnsafeUtil (file:/home/ofer/.cache/bazel/_bazel_ofer/e4549c5944aaba63959aeb34851601b9/external/mvncom_google_protobuf_protobuf_java/jar/mvncom_google_protobuf_protobuf_java.jar) to field java.nio.Buffer.address
WARNING: Please consider reporting this to the maintainers of com.google.protobuf.UnsafeUtil
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
Target //projects/opentrain/gtfs_pipeline:data_analysis_part1 up-to-date:
  bazel-genfiles/projects/opentrain/gtfs_pipeline/calendar.protobin
  bazel-genfiles/projects/opentrain/gtfs_pipeline/route.protobin
  bazel-genfiles/projects/opentrain/gtfs_pipeline/stopTime.protobin
  bazel-genfiles/projects/opentrain/gtfs_pipeline/stops.protobin
  bazel-genfiles/projects/opentrain/gtfs_pipeline/translatations.protobin
  bazel-genfiles/projects/opentrain/gtfs_pipeline/trips.protobin
INFO: Elapsed time: 97.613s, Critical Path: 82.18s
INFO: 10 processes: 8 linux-sandbox, 2 worker.
INFO: Build completed successfully, 14 total actions
```

The output are the files in:

`bazel-genfiles/projects/opentrain/gtfs_pipeline/...`


## How to run israel_railways_pipeline

`bazel build //projects/opentrain/israel_railways_pipeline:open_train`

### Output:

```
INFO: Invocation ID: 0024f893-ce96-4ed7-a4c7-4c2e0d3c0a23
INFO: SHA256 (https://firebasestorage.googleapis.com/v0/b/startupos-5f279.appspot.com/o/data_csv.zip?alt=media&token=2a262967-7cdd-4934-afbc-8fa31a74baf2) = b92c70262552798ada24016a7bcb33354ebfd83132d84fa23c2164599eec81e4
INFO: Analysed target //projects/opentrain/israel_railways_pipeline:open_train (8 packages loaded, 41 targets configured).
INFO: Found 1 target...
Target //projects/opentrain/israel_railways_pipeline:open_train up-to-date:
  bazel-genfiles/projects/opentrain/israel_railways_pipeline/output.txt
INFO: Elapsed time: 24.023s, Critical Path: 8.01s
INFO: 3 processes: 2 linux-sandbox, 1 worker.
INFO: Build completed successfully, 7 total actions
```

The output is `bazel-genfiles/projects/opentrain/israel_railways_pipeline/output.txt`.
