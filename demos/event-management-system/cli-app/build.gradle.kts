plugins {
    id("com.example.javapreview-conventions")
    application
}

group = "com.example.eventmanagement"
version = "1.0.0"

application {
    mainClass.set("com.example.eventmanagement.cli.EventManagementCli")
}

dependencies {
    implementation("com.example.core:common:1.0.0")
    implementation("com.example.core:domain:1.0.0")
    implementation("com.example.core:operations:1.0.0")
}
