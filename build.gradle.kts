import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.1"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.spring") version "1.4.21"
}

group = "nl.juraji"
version = "3.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    val neo4jSpringBootVersion = "4.1.1.1"
    val neo4jDriverVersion = "4.2.0"
    val liquigraphVersion = "4.0.2"

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.neo4j.driver:neo4j-java-driver-spring-boot-starter:$neo4jSpringBootVersion")
    implementation("org.neo4j.driver:neo4j-java-driver:$neo4jDriverVersion")
    implementation("org.liquigraph:liquigraph-core:$liquigraphVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.neo4j.driver:neo4j-java-driver-test-harness-spring-boot-autoconfigure:$neo4jSpringBootVersion")
    testImplementation("org.neo4j.test:neo4j-harness:$neo4jDriverVersion") {
        exclude("org.slf4j", "slf4j-nop")
    }
    testImplementation("io.projectreactor:reactor-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = java.targetCompatibility.majorVersion
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
