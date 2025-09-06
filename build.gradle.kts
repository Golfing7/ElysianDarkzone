plugins {
    kotlin("jvm") version "1.9.23"
    id("com.gradleup.shadow") version("8.3.6")
}

val libraryFolder = "locallibs"
val deployDirectory = "C:\\Users\\Miner\\DoomPvP\\plugins"
group = "com.golfing8"
version = "1.0"

repositories {
    mavenCentral()
    mavenLocal()

    flatDir {
        dir(rootDir.resolve(libraryFolder))
    }

    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    testImplementation(kotlin("test"))

    compileOnly("net.techcable.tacospigot:WineSpigot:1.8.8-R0.2-SNAPSHOT")
    compileOnly("com.golfing8:KCommon:1.1")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}

tasks.create("deploy") {
    dependsOn(tasks.shadowJar)

    doFirst {
        val outputFile = tasks.getByName("shadowJar").outputs.files.first()
        val targetFile = File(deployDirectory, "${project.name}-${project.version}.jar")

        outputFile.copyTo(targetFile, overwrite = true)
    }
}