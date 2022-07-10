plugins {
    id("java")
}

group = "com.gamerduck.kitpvp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("com.github.Minestom:Minestom:9181c52e6b")
    implementation("org.projectlombok:lombok:1.18.24")
}