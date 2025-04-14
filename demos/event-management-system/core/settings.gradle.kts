pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "core"

include(":common")
include(":domain")
include(":operations")
