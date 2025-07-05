plugins {
    kotlin("jvm") version "2.1.21"
}

group = "com.yifeistudio"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    compileOnly("io.fabric8:kubernetes-client:7.3.1")
    compileOnly("com.alibaba.nacos:nacos-client:3.0.2")
    compileOnly("org.springframework.boot:spring-boot-starter-data-redis:3.5.3")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}