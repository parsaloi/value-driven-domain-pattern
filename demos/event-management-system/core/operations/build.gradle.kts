plugins {
    id("core-library")
}
group = "com.example.core.operations"
version = "1.0.0"

dependencies {
    api(project(":common"))
    api(project(":domain"))
}
