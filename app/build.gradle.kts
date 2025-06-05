import java.util.Properties

plugins {
    alias(notation = libs.plugins.android.application)
    alias(notation = libs.plugins.kotlin.android)
    alias(notation = libs.plugins.googlePlayServices)
    alias(notation = libs.plugins.googleFirebase)
}

android {
    namespace = "com.d4rk.lowbrightness"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.d4rk.lowbrightness"
        minSdk = 26 // TODO: Make 23 when icon is ready
        targetSdk = 36
        versionCode = 22
        versionName = "5.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        @Suppress("UnstableApiUsage") androidResources.localeFilters += listOf(
            "ar-rEG" , "bg-rBG" , "bn-rBD" , "de-rDE" , "en" , "es-rGQ" , "es-rMX" , "fil-rPH" , "fr-rFR" , "hi-rIN" , "hu-rHU" , "in-rID" , "it-rIT" , "ja-rJP" , "ko-rKR" , "pl-rPL" , "pt-rBR" , "ro-rRO" , "ru-rRU" , "sv-rSE" , "th-rTH" , "tr-rTR" , "uk-rUA" , "ur-rPK" , "vi-rVN" , "zh-rTW"
        )
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release")

        val signingProps = Properties()
        val signingFile = rootProject.file("signing.properties")

        if (signingFile.exists()) {
            signingProps.load(signingFile.inputStream())

            signingConfigs.getByName("release").apply {
                storeFile = file(signingProps["STORE_FILE"].toString())
                storePassword = signingProps["STORE_PASSWORD"].toString()
                keyAlias = signingProps["KEY_ALIAS"].toString()
                keyPassword = signingProps["KEY_PASSWORD"].toString()
            }
        }
        else {
            android.buildTypes.getByName("release").signingConfig = null
        }
    }

    buildTypes {
        release {
            val signingFile = rootProject.file("signing.properties")
            signingConfig = if (signingFile.exists()) {
                signingConfigs.getByName("release")
            } else {
                null
            }
            isDebuggable = false
        }
        debug {
            isDebuggable = true
        }
    }

    buildTypes.forEach { buildType ->
        with(receiver = buildType) {
            multiDexEnabled = true
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile(name = "proguard-android-optimize.txt") , "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    packaging {
        resources {
            excludes.add("META-INF/INDEX.LIST")
            excludes.add("META-INF/io.netty.versions.properties")
        }
    }
}

dependencies {
    implementation("com.google.firebase:firebase-crashlytics:19.4.3")
    implementation("com.google.firebase:firebase-analytics:22.4.0")
    implementation("com.google.firebase:firebase-perf:21.0.5")
    implementation("com.google.android.gms:play-services-oss-licenses:17.1.0")
    implementation("com.google.android.gms:play-services-ads:24.3.0")
    implementation("com.google.android.play:review-ktx:2.0.2")
    implementation("com.google.android.play:app-update-ktx:2.1.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.gridlayout:gridlayout:1.1.0")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0")
    implementation("androidx.lifecycle:lifecycle-process:2.9.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.9.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("com.airbnb.android:lottie:6.6.0")
    implementation("com.wdullaer:materialdatetimepicker:4.2.3")
    implementation("com.thebluealliance:spectrum:0.7.1")
    implementation("me.zhanghai.android.fastscroll:library:1.3.0")
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    testImplementation(libs.kotlinx.coroutines.test)
}