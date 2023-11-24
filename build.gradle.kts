group = "com.github.gavlyukovskiy"
version = "0.0.1-SNAPSHOT"

plugins {
    val kotlinVersion = "1.9.20"
    kotlin("jvm") version kotlinVersion apply false
    kotlin("plugin.spring") version kotlinVersion apply false
    id("org.springframework.boot") version "3.2.0" apply false
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
