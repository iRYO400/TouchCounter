import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.googleKsp)
}

android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "workshop.akbolatss.tools.touchcounter"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        versionCode = 12
        versionName = "1.2.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    namespace = defaultConfig.applicationId

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(JavaVersion.VERSION_17.toString())
        }
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
        arg("room.expandProjection", "true")
    }

    viewBinding.isEnabled = true

    testOptions {
        unitTests.isIncludeAndroidResources = true
        execution = ("ANDROIDX_TEST_ORCHESTRATOR")
    }
    buildFeatures {
        buildConfig = true
    }

    ktlint {
        android = true
        disabledRules = listOf(
            "argument-list-wrapping",
            "import-ordering",
            "max-line-length",
            "wrapping",
            "multiline-if-else",
        )
    }
}

tasks.withType(Test::class.java).configureEach {
    filter {
        includeTestsMatching("workshop.akbolatss.tools.touchcounter.AllTestSuites")
    }
}

dependencies {
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlin.coroutines.android)

    implementation(libs.material)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.timber)

    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room)
    ksp(libs.androidx.room.compiler)

    implementation(libs.dagger)
    ksp(libs.dagger.compiler)
    implementation(libs.dagger.android)
    implementation(libs.dagger.android.support)
    ksp(libs.dagger.android.processor)

    androidTestImplementation(libs.androidx.runner)

    androidTestImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.junit)

    androidTestImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.kotlin)

    androidTestImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.livedata.testing.ktx)
    testImplementation(libs.livedata.testing.ktx)

    androidTestImplementation(libs.androidx.core.testing)
    testImplementation(libs.androidx.core.testing)

    androidTestImplementation(libs.google.truth)
    testImplementation(libs.google.truth)

    testImplementation(libs.robolectric)
}
