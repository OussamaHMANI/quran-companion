plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.quoren.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.quoren.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    lint {
        // Media3's UnstableApi is a Java-only opt-in marker that Kotlin and lint cannot be told to accept globally.
        disable += "UnsafeOptInUsageError"
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)
    implementation(libs.media3.datasource)
    implementation(libs.media3.database)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.okhttp)
    implementation(libs.vosk.android)
    implementation(libs.adhan.java)
    implementation("org.osmdroid:osmdroid-android:6.1.20")

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
