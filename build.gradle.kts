val ktorVersion = "1.3.2"

plugins {
    application
    kotlin("jvm") version "1.3.70"
    id("org.jlleitschuh.gradle.ktlint") version "8.2.0"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

application {
    mainClassName = "swiftstaff.MainKt"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.apache.commons:commons-text:1.8")
    implementation("com.xenomachina:kotlin-argparser:2.0.7")
    runtime(group = "org.apache.commons", name = "commons-lang3", version = "3.9")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("io.ktor:ktor-gson:$ktorVersion")
    implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.5")
    implementation(group = "org.slf4j", name = "slf4j-log4j12", version = "1.7.5")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
}

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
}

tasks {


    compileKotlin {
    }

    compileTestKotlin {
    }

    test {
        useJUnitPlatform()
    }
}
