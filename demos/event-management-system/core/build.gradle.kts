plugins {
    base
    `maven-publish`
}

allprojects {
    group = "com.example.core"
    version = "1.0.0"
    repositories {
        mavenCentral()
        mavenLocal()
    }
}

subprojects {
    plugins.apply("maven-publish")

    // Ensure Java plugin is applied (already handled by core-library, but for safety)
    plugins.apply("java-library")

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()
            }
        }
        repositories {
            mavenLocal() // Publish to Maven Local
        }
    }
}
