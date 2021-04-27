import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.spring") version "1.4.32"
    id("com.google.cloud.tools.jib") version "3.0.0"
}

buildscript {
    dependencies {
        classpath("com.google.cloud.tools:jib-spring-boot-extension-gradle:0.1.0")
    }
}

val group = "nl.juraji"
val version = "3.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_14
java.targetCompatibility = JavaVersion.VERSION_14

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.github.Juraji:reactor-validations:master-SNAPSHOT")
    implementation("com.sksamuel.scrimage:scrimage-core:4.0.16")
    implementation("com.sksamuel.scrimage:scrimage-filters:4.0.16")
    implementation("com.sksamuel.scrimage:scrimage-formats-extra:4.0.16")
    implementation("io.projectreactor.addons:reactor-extra")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.neo4j.driver:neo4j-java-driver-test-harness-spring-boot-autoconfigure:4.1.1.1")
    testImplementation("org.neo4j.test:neo4j-harness:4.2.0") {
        exclude("org.slf4j", "slf4j-nop")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.mockk:mockk:1.10.5")
    testImplementation("com.ninja-squad:springmockk:3.0.1")
    testImplementation("com.github.marcellogalhardo:kotlin-fixture:0.0.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf(
            "-Xjsr305=strict",
            "-Xopt-in=kotlin.io.path.ExperimentalPathApi"
        )
        jvmTarget = java.targetCompatibility.majorVersion
        useIR = true
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    environment("spring_profiles_active", "test")
}

jib {
    from {
        image = "openjdk:14"
    }
    to {
        image = "albums-backend"
        tags = setOf("latest")
    }
    container {
        creationTime = "USE_CURRENT_TIMESTAMP"
    }
    pluginExtensions {
        pluginExtension {
            implementation = "com.google.cloud.tools.jib.gradle.extension.springboot.JibSpringBootExtension"
        }
    }
}
