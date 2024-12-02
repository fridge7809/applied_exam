plugins {
    id("java")
    id("me.champeau.jmh") version "0.7.2"
}

group = "org.aaa"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // https://mvnrepository.com/artifact/net.jqwik/jqwik
    testImplementation("net.jqwik:jqwik:1.9.1")
    testImplementation("org.junit.jupiter:junit-jupiter")
    // https://mvnrepository.com/artifact/org.assertj/assertj-core
    testImplementation("org.assertj:assertj-core:3.26.3")
}

tasks.test {
    useJUnitPlatform()
}

jmh {
    iterations = 1
    fork = 0
    timeUnit = "us"
    resultFormat = "CSV"
    warmupIterations = 0
}