plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.acledaproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.acledaproject"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }

    dataBinding {
        enable = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Splash screen
    implementation("androidx.core:core-splashscreen:1.1.0-alpha02")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    // Scan qr zxing
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // Scan CameraX dependencies with mlkit
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.4.0-alpha03")

    // ML Kit barcode scanning dependency
    implementation("com.google.mlkit:barcode-scanning:17.2.0")
    implementation("com.google.android.gms:play-services-vision:20.1.3")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")


    implementation("com.github.yuriy-budiyev:code-scanner:2.3.0")

    implementation("com.github.bobekos:SimpleBarcodeScanner:1.0.23")

    // EMV Generate QR
    implementation("com.github.mvallim:emv-qrcode:0.1.2") {
        exclude(group = "org.aspectj", module = "aspectjrt")
    }

    //KH QR
    // implementation("kh.gov.nbc.bakong_khqr:sdk-java:1.0.6")
}