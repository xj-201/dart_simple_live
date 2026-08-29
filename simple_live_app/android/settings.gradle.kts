pluginManagement {
    val flutterSdkPath =
        run {
            val properties = java.util.Properties()
            file("local.properties").inputStream().use { properties.load(it) }
            val flutterSdkPath = properties.getProperty("flutter.sdk")
            require(flutterSdkPath != null) { "flutter.sdk not set in local.properties" }
            flutterSdkPath
        }

    includeBuild("$flutterSdkPath/packages/flutter_tools/gradle")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.flutter.flutter-plugin-loader") version "1.0.0"
    // 将 com.android.application 调整为广泛兼容的 8.2.1 或 8.3.0 稳定版
    id("com.android.application") version "8.2.1" apply false
    // 保持与 Flutter 3.38 工具链一致的 Kotlin 插件版本
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
}

include(":app")
