plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android") version "2.2.20"
}

android {
    namespace = "io.ionic.liveupdateprovidermock"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    implementation("io.ionic:liveupdateprovider:1.0.0")
    implementation("com.capacitorjs:core:[8.0.0,9.0.0)")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}
