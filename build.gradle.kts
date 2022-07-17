plugins {
    id("java")
    id("maven-publish")
    id("io.freefair.lombok") version "6.4.1"
}

group = "ml.empee"
version = "0.1"

repositories {
    //Spigot Repo
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    //Mojang Repo
    maven("https://libraries.minecraft.net/")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")

    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")

    implementation("me.lucko:commodore:1.13")
    implementation("net.kyori:adventure-platform-bukkit:4.1.0")
}



publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "commandsManager"
            from(components["java"])
        }
    }
}

