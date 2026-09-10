plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.velcat.charactercore"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.velcat.charactercore"
        minSdk = 26
        targetSdk = 35
        versionCode = 4
        versionName = "0.3.1-alpha"
    }

    flavorDimensions += "character"
    productFlavors {
        create("velket") {
            dimension = "character"
            applicationIdSuffix = ".velket"
            versionNameSuffix = "-velket"
            buildConfigField("String", "CHARACTER_ID", "\"velket\"")
        }
        create("starcat") {
            dimension = "character"
            applicationIdSuffix = ".starcat"
            versionNameSuffix = "-starcat"
            buildConfigField("String", "CHARACTER_ID", "\"starcat\"")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}
