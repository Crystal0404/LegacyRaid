pluginManagement {
    repositories {
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.architectury.dev/") }
        if (System.getenv("CI") != "true" /* not run in github actions */ ) {
            // If you're not from China, please remove this, it will slow down your downloads
            maven {
                url = uri("https://maven.aliyun.com/repository/gradle-plugin")
                content {
                    excludeGroup("me.modmuss50")
                }
            }
        }
        mavenCentral()
        maven { url = uri("https://maven.neoforged.net/releases") }
        gradlePluginPortal()
    }
}

rootProject.name = "LegacyRaid"

include("common")
include("fabric")
include("neoforge")
