plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.ktlint) apply(false)
    alias(libs.plugins.googleKsp) apply(false)
}
