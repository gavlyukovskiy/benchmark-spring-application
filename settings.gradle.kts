rootProject.name = "benchmark-spring-application"

fileTree("./")
    .filter { it.name == "build.gradle.kts" }
    .forEach { include(":${it.parentFile.name}") }


pluginManagement {
    plugins {
        id("org.springframework.boot") version "3.0.0"
        id("io.spring.dependency-management") version "1.1.0"
        id("com.github.ben-manes.versions") version "0.44.0"
    }
}
