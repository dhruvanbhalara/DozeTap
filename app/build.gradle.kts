import java.io.FileInputStream
import java.util.Properties

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystorePropertiesFile.inputStream().use { keystoreProperties.load(it) }
}

fun getKeystoreProperty(vararg keys: String): String? {
    for (key in keys) {
        val envVal = System.getenv(key)
        if (!envVal.isNullOrBlank()) return envVal
        val propVal = keystoreProperties.getProperty(key)
        if (!propVal.isNullOrBlank()) return propVal
    }
    return null
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.dhruvanbhalara.dozetap"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dhruvanbhalara.dozetap"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    flavorDimensions += "version"
    productFlavors {
        create("prod") {
            dimension = "version"
            manifestPlaceholders["appName"] = "DozeTap"
        }
        create("dev") {
            dimension = "version"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            manifestPlaceholders["appName"] = "DozeTap Dev"
        }
    }

    signingConfigs {
        getByName("debug") {
            val debugFile = getKeystoreProperty("DEBUG_KEYSTORE_FILE", "KEYSTORE_FILE_DEBUG")
            if (debugFile != null && file(debugFile).exists()) {
                storeFile = file(debugFile)
                storePassword = getKeystoreProperty("DEBUG_KEYSTORE_PASSWORD", "KEYSTORE_PASSWORD_DEBUG") ?: "android"
                keyAlias = getKeystoreProperty("DEBUG_KEY_ALIAS", "KEY_ALIAS_DEBUG") ?: "androiddebugkey"
                keyPassword = getKeystoreProperty("DEBUG_KEY_PASSWORD", "KEY_PASSWORD_DEBUG") ?: "android"
            }
        }

        create("release") {
            val releaseFile = getKeystoreProperty("RELEASE_KEYSTORE_FILE", "KEYSTORE_FILE", "SIGNING_KEY_FILE")
            if (releaseFile != null && file(releaseFile).exists()) {
                storeFile = file(releaseFile)
                storePassword = getKeystoreProperty("RELEASE_KEYSTORE_PASSWORD", "KEYSTORE_PASSWORD")
                keyAlias = getKeystoreProperty("RELEASE_KEY_ALIAS", "KEY_ALIAS")
                keyPassword = getKeystoreProperty("RELEASE_KEY_PASSWORD", "KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val releaseConfig = signingConfigs.findByName("release")
            if (releaseConfig?.storeFile != null && releaseConfig.storeFile!!.exists()) {
                signingConfig = releaseConfig
            } else {
                // Explicitly avoid silent debug key signing for release builds
                signingConfig = null
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INDEX/*"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)
    
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)
    implementation(libs.androidx.appcompat)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
}
