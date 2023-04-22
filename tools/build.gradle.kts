plugins {
    kotlin("jvm")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))

    implementation("org.springframework:spring-jdbc")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.zaxxer:HikariCP")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("ch.qos.logback:logback-classic")
}

tasks {
    val bootstrap by registering(JavaExec::class) {
        mainClass.set("com.github.gavlyukovskiy.bootstrap.BootstrapKt")
        classpath(sourceSets.main.get().runtimeClasspath)
    }
}
