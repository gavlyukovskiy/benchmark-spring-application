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
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    runtimeOnly("io.r2dbc:r2dbc-pool")
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    implementation("org.jooq:jooq")
    implementation("org.jooq:jooq-kotlin")
    implementation("org.jooq:jooq-kotlin-coroutines:${project.extra["jooq.version"]}")
    implementation("com.squareup.okhttp3:okhttp")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
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
}
