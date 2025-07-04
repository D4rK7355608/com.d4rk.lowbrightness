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
        minSdk = 23
        targetSdk = 36
        versionCode = 47
        versionName = "5.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        @Suppress("UnstableApiUsage") androidResources.localeFilters += listOf(
            "ar-rEG" , "bg-rBG" , "bn-rBD" , "de-rDE" , "en" , "es-rGQ" , "es-rMX" , "fil-rPH" , "fr-rFR" , "hi-rIN" , "hu-rHU" , "in-rID" , "it-rIT" , "ja-rJP" , "ko-rKR" , "pl-rPL" , "pt-rBR" , "ro-rRO" , "ru-rRU" , "sv-rSE" , "th-rTH" , "tr-rTR" , "uk-rUA" , "ur-rPK" , "vi-rVN" , "zh-rTW"
        )
        vectorDrawables {
            useSupportLibrary = true
        }

        val githubProps = Properties()
        val githubFile = rootProject.file("github.properties")
        val githubToken = if (githubFile.exists()) {
            githubProps.load(githubFile.inputStream())
            githubProps["GITHUB_TOKEN"].toString()
        } else {
            ""
        }
        buildConfigField("String", "GITHUB_TOKEN", "\"$githubToken\"")
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
    // App Core
    implementation(dependencyNotation = "com.github.D4rK7355608:AppToolkit:1.0.34") {
        isTransitive = true
    }

    implementation(dependencyNotation = libs.androidx.navigation.fragment.ktx)
    implementation(dependencyNotation = libs.androidx.navigation.ui.ktx)
    implementation(dependencyNotation = libs.preference.ktx)
    implementation(dependencyNotation = libs.materialdatetimepicker)
    implementation(dependencyNotation = libs.spectrum)
    implementation(dependencyNotation = libs.library)
    implementation(dependencyNotation = libs.compose.material3.window.size)
    implementation(dependencyNotation = libs.xxpermissions)
    implementation(dependencyNotation = libs.accompanist.navigation.animation)
    implementation(dependencyNotation = libs.profileinstaller)
    implementation(dependencyNotation = libs.compose.color.picker)
    implementation(dependencyNotation = libs.compose.color.picker.android)
}