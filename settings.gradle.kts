rootProject.name = "benchmark-spring-application"

fileTree("./")
    .filter { it.name == "build.gradle.kts" }
    .filter { it.parentFile.name != rootProject.name }
    .forEach { include(":${it.parentFile.name}") }

