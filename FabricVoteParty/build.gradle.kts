plugins {
    id("java")
    id("quiet-fabric-loom") version("1.0-SNAPSHOT")
    kotlin("jvm") version ("1.7.10")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    minecraft("com.mojang:minecraft:1.19.2")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:0.14.14")

    modImplementation("net.fabricmc.fabric-api:fabric-api:0.76.0+1.19.2")
    modImplementation("me.lucko:fabric-permissions-api:0.2-SNAPSHOT")

    modImplementation("net.kyori:adventure-platform-fabric:5.5.1")
    modImplementation("org.spongepowered:configurate-gson:4.1.2")

    include("net.kyori:adventure-platform-fabric:5.5.1")
    include("org.spongepowered:configurate-gson:4.1.2")

    modImplementation(fileTree("libs"))

}
