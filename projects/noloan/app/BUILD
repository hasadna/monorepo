android_binary(
    name = "app",
    srcs = glob(["*.java"]),
    custom_package = "projects.noloan.app",
    manifest = "AndroidManifest.xml",
    resource_files = glob(["res/**"]),
    assets_dir = "assets",
    assets = ["assets/google-services.json"],
    multidex='legacy',
    visibility = ["//visibility:public"],
    javacopts = ["-verbose"],
    deps = [
        "//projects/noloan/app/proto:data_protos_java",
        "@com_android_support_support_v4_27_0_2//aar",

        '//third_party/maven/com/google/guava:guava',
        '@com_google_protobuf//:protobuf_java',

        '@com_android_support_multidex_1_0_1//aar',
        '@com_android_support_appcompat_v7_27_0_2//aar',

        '@com_android_support_animated_vector_drawable_27_0_2//aar',
        '@com_android_support_support_annotations_27_0_2//jar',
        '@com_android_support_support_vector_drawable_27_0_2//aar',
        '@com_android_support_support_fragment_27_0_2//aar',


        '@com_android_support_support_media_compat_27_0_2//aar',
        '@com_android_support_support_compat_27_0_2//aar',
        '@com_android_support_support_core_ui_27_0_2//aar',
        '@com_android_support_design_27_0_2//aar',

        '@android_arch_lifecycle_runtime_1_0_3//aar',
        '@android_arch_lifecycle_common_1_0_3//jar',
        '@android_arch_core_common_1_0_0//jar',

        '@com_android_support_constraint_constraint_layout_1_0_2//aar',
        '@com_android_support_constraint_constraint_layout_solver_1_0_2//jar',

        '@com_android_support_support_core_utils_27_0_2//aar',
        '@com_android_support_cardview_v7_27_0_2//aar',
        '@com_android_support_recyclerview_v7_27_0_2//aar',

        "@startup_os//android:firestore",
        "@startup_os//android:firestore_config_manager",


    ],
)
