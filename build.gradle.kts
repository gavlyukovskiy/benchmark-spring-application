group = "com.github.gavlyukovskiy"
version = "0.0.1-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.8.21" apply false
    kotlin("plugin.spring") version "1.8.21" apply false
    id("org.springframework.boot") version "3.1.0" apply false
    id("com.github.ben-manes.versions") version "0.46.0" apply false
}

subprojects {
    tasks {
        withType<Test>().configureEach {
            useJUnitPlatform()
        }

        withType<JavaCompile> {
            targetCompatibility = "19"
        }

        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict")
                jvmTarget = "19"
            }
        }

        withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>().configureEach {
            fun isNonStable(version: String): Boolean {
                val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
                val regex = "^[0-9,.v-]+(-r)?$".toRegex()
                val isStable = stableKeyword || regex.matches(version)
                return isStable.not()
            }
            rejectVersionIf { isNonStable(candidate.version) }
        }

        withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>().configureEach {
            imageName.set(project.name)
            buildpacks.set(listOf("gcr.io/paketo-buildpacks/amazon-corretto", "urn:cnb:builder:paketo-buildpacks/java"))
            environment.set(
                mapOf(
                    "BP_JVM_TYPE" to "JDK",
                    "BP_JVM_VERSION" to "20",
                    "BPL_JVM_HEAD_ROOM" to "10"
                )
            )
        }
    }
}
