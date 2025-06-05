import java.util.Properties

plugins {
    alias(notation = libs.plugins.android.application)
    alias(notation = libs.plugins.kotlin.android)
    alias(notation = libs.plugins.kotlin.compose)
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
        compose = true
    }


    packaging {
        resources {
            excludes.add("META-INF/INDEX.LIST")
            excludes.add("META-INF/io.netty.versions.properties")
        }
    }
}

dependencies {
    implementation(dependencyNotation = libs.firebase.crashlytics)
    implementation(dependencyNotation = libs.firebase.analytics)
    implementation(dependencyNotation = libs.firebase.perf)
    implementation(dependencyNotation = libs.play.services.oss.licenses)
    implementation(dependencyNotation = libs.play.services.ads)
    implementation(dependencyNotation = libs.review.ktx)
    implementation(dependencyNotation = libs.app.update.ktx)
    implementation(dependencyNotation = libs.material)
    implementation(dependencyNotation = libs.androidx.appcompat)
    implementation(dependencyNotation = libs.androidx.core.ktx)
    implementation(dependencyNotation = libs.androidx.core.splashscreen)
    implementation(dependencyNotation = libs.androidx.constraintlayout)
    implementation(dependencyNotation = libs.androidx.gridlayout)
    implementation(dependencyNotation = libs.androidx.multidex)
    implementation(dependencyNotation = libs.androidx.navigation.fragment.ktx)
    implementation(dependencyNotation = libs.androidx.navigation.ui.ktx)
    implementation(dependencyNotation = libs.androidx.lifecycle.viewmodel.ktx)
    implementation(dependencyNotation = libs.androidx.lifecycle.livedata.ktx)
    implementation(dependencyNotation = libs.lifecycle.process)
    implementation(dependencyNotation = libs.androidx.lifecycle.common.java8)
    implementation(dependencyNotation = libs.preference.ktx)
    implementation(dependencyNotation = libs.lottie)
    implementation(dependencyNotation = libs.materialdatetimepicker)
    implementation(dependencyNotation = libs.spectrum)
    implementation(dependencyNotation = libs.androidx.work.runtime.ktx)
    implementation(dependencyNotation = libs.library)
    implementation(dependencyNotation = libs.kotlinx.coroutines.android)
    implementation(dependencyNotation = libs.kotlinx.coroutines.play.services)

    implementation(dependencyNotation = libs.compose.ui)
    implementation(dependencyNotation = libs.compose.material3)
    implementation(dependencyNotation = libs.compose.material3.window.size)
    implementation(dependencyNotation = libs.compose.material.icons.extended)
    implementation(dependencyNotation = libs.compose.ui.tooling.preview)
    implementation(dependencyNotation = libs.lifecycle.runtime.compose)
    implementation(dependencyNotation = libs.lifecycle.runtime.ktx)
    implementation(dependencyNotation = libs.activity.compose)
    implementation(dependencyNotation = libs.koin.android)
    implementation(dependencyNotation = libs.koin.android.compose)
    implementation(dependencyNotation = libs.xxpermissions)
    implementation(dependencyNotation = libs.navigation.compose)
    implementation(dependencyNotation = libs.accompanist.navigation.animation)
    implementation(dependencyNotation = libs.coil.compose)
    implementation(dependencyNotation = libs.profileinstaller)
    implementation(dependencyNotation = libs.compose.color.picker)
    implementation(dependencyNotation = libs.compose.color.picker.android)
    debugImplementation(dependencyNotation = libs.compose.ui.tooling)
    debugImplementation(dependencyNotation = libs.compose.ui.test.manifest)
}