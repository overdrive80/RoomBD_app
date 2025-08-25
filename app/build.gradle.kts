plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "edu.overdrive.roombd_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "edu.overdrive.roombd_app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Room desde el IDE
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // Room para Java
//    val version_room = "2.7.2"
//    implementation("androidx.room:room-runtime:$version_room")
//    annotationProcessor("androidx.room:room-compiler:$version_room")
}