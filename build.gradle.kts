group = "com.github.gavlyukovskiy"
version = "0.0.1-SNAPSHOT"

plugins {
    kotlin("jvm") version "2.0.21" apply false
    kotlin("plugin.spring") version "2.0.21" apply false
    id("org.springframework.boot") version "3.3.4" apply false
}

subprojects {
    tasks {
        withType<Test>().configureEach {
            useJUnitPlatform()
        }

        withType<JavaCompile> {
            targetCompatibility = "21"
        }

        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict")
                jvmTarget = "21"
            }
        }

        withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>().configureEach {
            imageName.set(project.name)
            buildpacks.set(listOf("gcr.io/paketo-buildpacks/adoptium", "urn:cnb:builder:paketo-buildpacks/java"))
            environment.set(
                mapOf(
                    "BP_JVM_TYPE" to "JDK",
                    "BP_JVM_VERSION" to "21",
                    "BPL_JVM_HEAD_ROOM" to "10"
                )
            )
        }
    }
}
