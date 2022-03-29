plugins {
    id("java")
    id("maven-publish")
    id("io.freefair.lombok") version "6.4.1"
}

group = "tk.empee"
version = "1.0"

repositories {

    //Spigot Repo
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    //Brigadier Repo
    maven("https://libraries.minecraft.net/")

    mavenCentral()
}

dependencies {

    implementation("me.lucko:commodore:1.13")

    compileOnly("org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT")

}



publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "commandManager"
            from(components["java"])
        }
    }
}

