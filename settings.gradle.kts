rootProject.name = "benchmark-spring-reactive"

fileTree("./")
    .filter { it.name == "build.gradle.kts" }
    .forEach { include(":${it.parentFile.name}") }


pluginManagement {
    plugins {
        id("org.springframework.boot") version "2.7.3"
        id("io.spring.dependency-management") version "1.0.13.RELEASE"
        kotlin("jvm") version "1.7.10"
        kotlin("plugin.spring") version "1.7.10"
    }
}