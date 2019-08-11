# Projects

## angular/angular-proto-firestore
An template project that shows how to use Angular + Protocol Buffers.

See [README](angular/angular-proto-firestore/README.md).


## angular/mysadna
A website that shows Hasadna projects and contributors:
http://mysadna.hasadna.org.il
* Projects have a simple page describing the project.
* Contributors have a personal profile page, showcasing their skills,
contributions and other profile links (LinkedIn, Twitter etc).

Website content can be modified by sending a PR.


See [README](angular/mysadna/README.md).


## angular/program-website
A website similar to mysadna, focused on the projects and participants in 
the Open Source Development Program with The Joint.


See [README](angular/program-website/README.md).


## angular/storyteller-tracker
A webapp that allows you to share development "moments" (e.g when you solve a bug,
or a screenshot of something surprising you saw while developing).
You can also track time while developing.

See [README](angular/storyteller-tracker/README.md).


## angular/storyteller-web
A webapp that shows the shared moments of others on your team.

See [README](angular/storyteller-web/README.md).


## avodata
A research project to help gather "soft attributes" for professions, to help people choose their next profession.

See [README](avodata/README.md).


## data_analysis
A template project for a data analysis pipeline.

The pipeline uses Bazel, which treats it as it would a compilation.
The benefit of this pipeline are:
* Reproducibility - everything needed to run the pipeline, including the data, is defined in the repo.
* Caching - the pipeline caches previous steps automatically (and knows when to rerun).
* Paralellism - the pipeline can take advantage of multiple cores.

For a talk on the pipeline given at the [Big Data Analytics Israel meetup](https://www.meetup.com/Big-Data-Analytics-Israel/events/259235551), see:
* [Part 1](https://www.youtube.com/watch?v=XGc1sIsYgvM&list=PLn2GS4h9ia-bp74rtKnoMFsFyYn4RgBLR&index=1)
* [Part 2](https://www.youtube.com/watch?v=hBuasmcYS00&list=PLn2GS4h9ia-bp74rtKnoMFsFyYn4RgBLR&index=2)
* [Part 1+2 slides-only video](https://www.youtube.com/watch?v=64qcGYdM4JE)

See [README](data_analysis/README.md).


## noloan
An Android app that helps fight SMS spam by helping people sue the spam senders.

See [README](noloan/README.md).


## opentrain
Pipelines that help analyse Israel Railways train data. It uses the `data_analysis` pipeline.
* [gtfs_pipeline](opentrain/gtfs_pipeline) analyses the GTFS data.
* [israel_railways_pipeline](opentrain/israel_railways_pipeline) analyses the Israel Railways data.

See [README](opentrain/README.md).

