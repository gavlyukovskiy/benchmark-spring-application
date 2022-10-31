import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
}

repositories {
    mavenCentral()
}

extra["jooq.version"] = "3.17.4"

dependencies {
    implementation(platform("software.amazon.awssdk:bom:2.17.261"))

    implementation("software.amazon.awssdk:dynamodb")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
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
}
