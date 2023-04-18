plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
    alias(libs.plugins.app.cash.sqldelight)
}

android {
    namespace = "com.rainy.mastodroid"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.rainy.mastodroid"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    lint {
        enable += "ComposeM2Api"
        error += "ComposeM2Api"
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.rainy.mastodroid")
        }
    }
}

dependencies {
    lintChecks(libs.compose.lint.checks)

    implementation(libs.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.viewmodel.savedstate)
    implementation(libs.lifecycle.common.java8)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.kotlinx.datetime)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.navigation.compose)
    implementation(libs.koin.androidx.compose)
    implementation(libs.datastore.preferences)
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.video)
    implementation(libs.coil.gif)
    implementation(libs.jsoup)
    implementation(libs.paging.runtime.ktx)
    implementation(libs.paging.compose)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation(libs.browser)
    implementation(libs.sqldelight.android.driver)
    implementation(libs.sqldelight.coroutines.extensions)
    implementation(libs.sqldelight.primitive.adapters)
    implementation(libs.sqlite.framework)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material)
    implementation(libs.compose.ui.util)
    implementation(libs.activity.compose)
    implementation(libs.accompanist.webview)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
