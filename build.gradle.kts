group = "com.github.gavlyukovskiy"
version = "0.0.1-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.7.21" apply false
    kotlin("plugin.spring") version "1.7.21" apply false
    id("org.springframework.boot") version "3.0.0" apply false
    id("io.spring.dependency-management") version "1.1.0" apply false
    id("com.github.ben-manes.versions") version "0.44.0" apply false
}

subprojects {
    tasks {
        withType<Test>().configureEach {
            useJUnitPlatform()
        }

        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict")
                jvmTarget = "17"
            }
        }

        withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>().configureEach {
            fun isNonStable(version: String): Boolean {
                val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
                val regex = "^[0-9,.v-]+(-r)?$".toRegex()
                val isStable = stableKeyword || regex.matches(version)
                return isStable.not()
            }
            rejectVersionIf { isNonStable(candidate.version) }
        }

        withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>().configureEach {
            imageName.set(project.name)
            buildpacks.set(listOf("gcr.io/paketo-buildpacks/adoptium", "urn:cnb:builder:paketo-buildpacks/java"))
        }
    }
}
