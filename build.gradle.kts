plugins {
    kotlin("jvm") version "1.9.23"
}

group = "com.golfing8"
version = "1.0"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation(kotlin("test"))

    compileOnly("net.techcable.tacospigot:WineSpigot:1.8.8-R0.2-SNAPSHOT")
    compileOnly("com.golfing8:KCommon:1.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}