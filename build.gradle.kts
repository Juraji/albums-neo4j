import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.spring") version "1.4.30"
}

group = "nl.juraji"
version = "3.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_14
java.targetCompatibility = JavaVersion.VERSION_14

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("io.projectreactor.addons:reactor-extra")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.liquigraph:liquigraph-core:4.0.2")
    implementation("com.github.Juraji:reactor-validations:master-SNAPSHOT")
    implementation("com.sksamuel.scrimage:scrimage-core:4.0.16")
    implementation("com.sksamuel.scrimage:scrimage-formats-extra:4.0.16")
    implementation("com.sksamuel.scrimage:scrimage-filters:4.0.16")
    implementation("org.springdoc:springdoc-openapi-webflux-ui:1.5.4")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
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
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = java.targetCompatibility.majorVersion
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    environment("spring_profiles_active", "test")
}
