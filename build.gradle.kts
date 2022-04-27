plugins {
    id("java")
    id("maven-publish")
    id("io.freefair.lombok") version "6.4.1"
}

group = "tk.empee"
version = "1.0"

repositories {

    //Paper Repo
    maven(url = "https://papermc.io/repo/repository/maven-public/")
    //Brigadier Repo
    maven("https://libraries.minecraft.net/")

    mavenCentral()
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT")

    implementation("me.lucko:commodore:1.13")
    implementation("net.kyori:adventure-platform-bukkit:4.1.0")
}



publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "commandsFramework"
            from(components["java"])
        }
    }
}

