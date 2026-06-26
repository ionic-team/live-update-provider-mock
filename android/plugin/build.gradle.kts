plugins {
    id("com.android.library")
}

android {
    namespace = "io.ionic.liveupdateprovidermock.plugin"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":core"))
    implementation("com.capacitorjs:core:[8.0.0,9.0.0)")
}
