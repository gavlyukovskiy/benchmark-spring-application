import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:2.7.3"))
    implementation(platform("software.amazon.awssdk:bom:2.17.261"))

    implementation("software.amazon.awssdk:dynamodb")

    implementation("org.springframework:spring-jdbc")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.zaxxer:HikariCP")
    implementation("org.postgresql:postgresql")
    implementation("ch.qos.logback:logback-classic")
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

    val bootstrap by registering(JavaExec::class) {
        mainClass.set("com.github.gavlyukovskiy.bootstrap.BootstrapKt")
        classpath(sourceSets.main.get().runtimeClasspath)
    }
}
