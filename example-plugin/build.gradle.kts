plugins {
    id("java")
}

group = "tk.empee"
version = "1.0"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("tk.empee:commandsFramework:1.0")
    compileOnly("org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT")
}