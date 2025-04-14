pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "event-management-system"

includeBuild("core") {
    dependencySubstitution {
        substitute(module("com.example.core:common")).using(project(":common"))
        substitute(module("com.example.core:domain")).using(project(":domain"))
        substitute(module("com.example.core:operations")).using(project(":operations"))
    }
}

includeBuild("cli-app")
