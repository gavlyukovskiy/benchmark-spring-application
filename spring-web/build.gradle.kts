plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.github.ben-manes.versions")
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
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    runtimeOnly("org.postgresql:postgresql:42.6.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.squareup.okhttp3:okhttp")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    withType<JavaExec>().configureEach {
        jvmArgs = listOf("--enable-preview")
    }

    withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>().configureEach {
        if (project.hasProperty("jetty")) {
            imageName.set("${project.name}-jetty")
        } else {
            imageName.set("${project.name}-tomcat")
        }
    }
}
