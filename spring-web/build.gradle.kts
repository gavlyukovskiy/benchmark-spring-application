import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.spring") version "1.7.21"
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.github.ben-manes.versions")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
}

repositories {
    mavenCentral()
}

extra["jooq.version"] = "3.17.5"
extra["jakarta-servlet.version"] = "5.0.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude("org.springframework.boot", "spring-boot-starter-tomcat")
    }
    if (project.hasProperty("jetty")) {
        compileOnly("org.springframework.boot:spring-boot-starter-tomcat")
        implementation("org.springframework.boot:spring-boot-starter-jetty")
        implementation("org.eclipse.jetty.http2:http2-server")
    } else {
        implementation("org.springframework.boot:spring-boot-starter-tomcat")
        compileOnly("org.springframework.boot:spring-boot-starter-jetty")
        compileOnly("org.eclipse.jetty.http2:http2-server")
    }
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jooq:jooq")
    implementation("org.jooq:jooq-kotlin")
    implementation("com.squareup.okhttp3:okhttp")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    withType<Test>().configureEach {
        useJUnitPlatform()
    }

    withType<JavaExec>().configureEach {
        jvmArgs = listOf("--enable-preview")
    }

    withType<DependencyUpdatesTask>().configureEach {
        fun isNonStable(version: String): Boolean {
            val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
            val regex = "^[0-9,.v-]+(-r)?$".toRegex()
            val isStable = stableKeyword || regex.matches(version)
            return isStable.not()
        }
        rejectVersionIf { isNonStable(candidate.version) }
    }
}
