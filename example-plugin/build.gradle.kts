plugins {
    id("com.github.johnrengelman.shadow").version("7.1.1")

    id("java")
}

group = "tk.empee"
version = "1.0"

repositories {
    //Spigot Repo
    maven(url = "https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("tk.empee:commandsFramework:1.0")
    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
}