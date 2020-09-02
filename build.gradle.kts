import java.util.UUID

plugins {
    kotlin("jvm") version "1.4.0"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

group = "net.azisaba"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    compileOnly("com.destroystokyo.paper:paper-api:1.12.2-R0.1-SNAPSHOT")
    implementation("io.github.leonardosnt:bungeechannelapi:1.0.0-SNAPSHOT")
}

tasks {
    compileKotlin { kotlinOptions.jvmTarget = "1.8" }
    compileTestKotlin { kotlinOptions.jvmTarget = "1.8" }

    shadowJar {
        relocate("kotlin", UUID.randomUUID().toString())
        relocate("org.jetbrains.annotations", UUID.randomUUID().toString())
        relocate("io.github.leonardosnt.bungeechannelapi", UUID.randomUUID().toString())

        minimize()
    }
}
