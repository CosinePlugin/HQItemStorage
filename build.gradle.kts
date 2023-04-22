plugins {
    id("maven-publish")
    kotlin("jvm") version "1.8.0"
}

group = "kr.hqservice.storage"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    compileOnly("com.github.MinseoServer", "MS-Core", "1.0.18")
    compileOnly("org.spigotmc", "spigot", "1.19.3-R0.1-SNAPSHOT")
    compileOnly("com.comphenix.protocol", "ProtocolLib", "5.0.0-SNAPSHOT")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveFileName.set("${rootProject.name}-${project.version}.jar")
    destinationDirectory.set(File("D:\\서버\\1.19.3 - 개발\\plugins"))
}

kotlin {
    jvmToolchain(17)
}