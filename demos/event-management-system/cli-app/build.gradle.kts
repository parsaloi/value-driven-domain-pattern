plugins {
    id("com.example.javapreview-conventions")
    application
}

repositories {
    mavenCentral()
    mavenLocal()
}

group = "com.example.eventmanagement"
version = "1.0.0"

application {
    mainClass.set("com.example.eventmanagement.cli.EventManagementCli")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.example.eventmanagement.cli.EventManagementCli"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

dependencies {
    implementation("com.example.core:common:1.0.0")
    implementation("com.example.core:domain:1.0.0")
    implementation("com.example.core:operations:1.0.0")
}
