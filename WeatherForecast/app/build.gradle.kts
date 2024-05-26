plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.weatherforecast"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weatherforecast"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources=true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}


dependencies {
    implementation ("com.airbnb.android:lottie:4.2.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.android.volley:volley:1.2.1")
    implementation ("com.google.firebase:firebase-functions:20.1.0")
    implementation ("com.google.firebase:firebase-bom:31.0.1")
    implementation ("com.google.firebase:firebase-analytics")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

