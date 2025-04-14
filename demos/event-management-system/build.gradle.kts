plugins {
    base
}

allprojects {
    group = "com.example"
    version = "1.0.0-SNAPSHOT"
}

repositories {
    mavenCentral()
}

tasks.register("buildInOrder") {
    group = "build"
    description = "Builds projects in order: core, then cli-app"
    dependsOn(gradle.includedBuild("core").task(":build"))
    doLast {
        gradle.includedBuild("cli-app").task(":build")
    }
}

tasks.register("cleanAll") {
    group = "build"
    description = "Cleans all projects"
    doLast {
        gradle.includedBuild("core").task(":clean")
        gradle.includedBuild("cli-app").task(":clean")
    }
}

defaultTasks("buildInOrder")
