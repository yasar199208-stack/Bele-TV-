plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.belestv"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.belestv"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.leanback:leanback:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // ExoPlayer (Media3) for stream playback, HLS/DASH/RTMP support
    implementation("androidx.media3:media3-exoplayer:1.4.0")
        implementation("androidx.media3:media3-exoplayer-hls:1.4.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.4.0")
    implementation("androidx.media3:media3-ui:1.4.0")
    implementation("androidx.media3:media3-ui-leanback:1.4.0")

    // Networking for fetching remote M3U / Xtream API
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Image loading for channel logos
    implementation("io.coil-kt:coil:2.6.0")
}
