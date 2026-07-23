import java.io.FileInputStream
import java.util.Properties

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystorePropertiesFile.inputStream().use { keystoreProperties.load(it) }
}

fun getKeystoreProperty(flavor: String? = null, vararg keys: String): String? {
    val flavorPrefix = flavor?.uppercase()?.let { "${it}_" } ?: ""
    for (key in keys) {
        val flavorKey = "$flavorPrefix$key"
        
        // Try flavor-specific environment variable then property
        val envValFlavor = System.getenv(flavorKey)
        if (!envValFlavor.isNullOrBlank()) return envValFlavor
        val propValFlavor = keystoreProperties.getProperty(flavorKey)
        if (!propValFlavor.isNullOrBlank()) return propValFlavor

        // Try general environment variable then property
        val envVal = System.getenv(key)
        if (!envVal.isNullOrBlank()) return envVal
        val propVal = keystoreProperties.getProperty(key)
        if (!propVal.isNullOrBlank()) return propVal
    }
    return null
}

fun resolveKeystoreFile(path: String?): File? {
    if (path == null) return null
    val f = file(path)
    if (f.exists()) return f
    val rootF = rootProject.file(path)
    if (rootF.exists()) return rootF
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

    signingConfigs {
        getByName("debug") {
            val debugFile = resolveKeystoreFile(getKeystoreProperty(null, "DEBUG_KEYSTORE_FILE", "KEYSTORE_FILE_DEBUG"))
            if (debugFile != null) {
                storeFile = debugFile
                storePassword = getKeystoreProperty(null, "DEBUG_KEYSTORE_PASSWORD", "KEYSTORE_PASSWORD_DEBUG") ?: "android"
                keyAlias = getKeystoreProperty(null, "DEBUG_KEY_ALIAS", "KEY_ALIAS_DEBUG") ?: "androiddebugkey"
                keyPassword = getKeystoreProperty(null, "DEBUG_KEY_PASSWORD", "KEY_PASSWORD_DEBUG") ?: "android"
            }
        }

        create("prodRelease") {
            val releaseFile = resolveKeystoreFile(getKeystoreProperty("prod", "RELEASE_KEYSTORE_FILE", "KEYSTORE_FILE", "SIGNING_KEY_FILE"))
            if (releaseFile != null) {
                storeFile = releaseFile
                storePassword = getKeystoreProperty("prod", "RELEASE_KEYSTORE_PASSWORD", "KEYSTORE_PASSWORD")
                keyAlias = getKeystoreProperty("prod", "RELEASE_KEY_ALIAS", "KEY_ALIAS")
                keyPassword = getKeystoreProperty("prod", "RELEASE_KEY_PASSWORD", "KEY_PASSWORD")
            }
        }

        create("devRelease") {
            val releaseFile = resolveKeystoreFile(getKeystoreProperty("dev", "RELEASE_KEYSTORE_FILE", "KEYSTORE_FILE", "SIGNING_KEY_FILE"))
            if (releaseFile != null) {
                storeFile = releaseFile
                storePassword = getKeystoreProperty("dev", "RELEASE_KEYSTORE_PASSWORD", "KEYSTORE_PASSWORD")
                keyAlias = getKeystoreProperty("dev", "RELEASE_KEY_ALIAS", "KEY_ALIAS")
                keyPassword = getKeystoreProperty("dev", "RELEASE_KEY_PASSWORD", "KEY_PASSWORD")
            }
        }
    }

    flavorDimensions += "version"
    productFlavors {
        create("prod") {
            dimension = "version"
            manifestPlaceholders["appName"] = "DozeTap"
            signingConfigs.findByName("prodRelease")?.let { config ->
                if (config.storeFile?.exists() == true) {
                    signingConfig = config
                }
            }
        }
        create("dev") {
            dimension = "version"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            manifestPlaceholders["appName"] = "DozeTap Dev"
            signingConfigs.findByName("devRelease")?.let { config ->
                if (config.storeFile?.exists() == true) {
                    signingConfig = config
                }
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
            signingConfig = null // Inherit from flavor
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
