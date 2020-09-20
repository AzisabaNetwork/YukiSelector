import java.util.*

plugins {
    kotlin("jvm") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

group = "net.azisaba"
version = "1.0.6"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("com.destroystokyo.paper:paper-api:1.12.2-R0.1-SNAPSHOT")
    implementation("org.jooq:joor-java-8:0.9.13")
}

tasks {
    compileKotlin { kotlinOptions.jvmTarget = "1.8" }
    compileTestKotlin { kotlinOptions.jvmTarget = "1.8" }

    processResources {
        filesMatching("**/plugin.yml") {
            filter { it.replace("%version", "$version") }
        }
    }

    shadowJar {
        relocate("kotlin", UUID.randomUUID().toString())
        relocate("org.jetbrains.annotations", UUID.randomUUID().toString())
        relocate("org.joor", UUID.randomUUID().toString())

        minimize()
    }
}
