import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("io.freefair.lombok") version "6.5.0.2"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "com.gamerduck.kitpvp"
version = "1.0-SNAPSHOT"
description = "DuckCommons"
java.sourceCompatibility = JavaVersion.VERSION_17


repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.24")
    implementation("com.github.Minestom:Minestom:9181c52e6b")
    implementation("com.electronwill.night-config:toml:3.6.5")
}
tasks {
    named<ShadowJar>("shadowJar") {
        manifest {
            attributes (
                "Main-Class" to "com.gamerduck.kitpvp.Main",
                "Multi-Release" to true
            )
        }
        archiveBaseName.set("KitPvP")
        mergeServiceFiles()
    }

    build { dependsOn(shadowJar) }
}