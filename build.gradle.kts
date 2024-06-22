plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"

    val indraVersion = "3.1.3"
    id("net.kyori.indra") version indraVersion
    id("net.kyori.indra.git") version indraVersion

    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.1"
}

group = "io.myzticbean"
version = "1.0.3"
description = "FindItemAddOn for QuickShop"

bukkitPluginYaml {
    main = "io.myzticbean.finditemaddon.FindItemAddOn"
    apiVersion = "1.20"
    authors = listOf("myzticbean","lukemango")
    depend = listOf("QuickShop-Hikari", "PlayerWarps", "HeadDatabase")
}

repositories {
    mavenCentral()
    maven ("https://jitpack.io")
    maven ("https://papermc.io/repo/repository/maven-public/")
    maven ("https://oss.sonatype.org/content/groups/public/")
    maven ("https://repo.olziedev.com/") // PlayerWarps
    maven ("https://repo.codemc.io/repository/maven-public/") // QuickShop-Hikari
    maven ("https://repo.mattstudios.me/artifactory/public/") // TriumphGui
}

dependencies {
    // Paper
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")

    // Cloud
    implementation("org.incendo:cloud-paper:2.0.0-beta.8")
    implementation("org.incendo:cloud-minecraft-extras:2.0.0-beta.8")
    implementation("org.incendo:cloud-annotations:2.0.0-rc.2")

    // PlayerWarps
    compileOnly("com.olziedev:playerwarps-api:6.30.0")

    // QuickShop-Hikari
    compileOnly("com.ghostchu:quickshop-bukkit:6.2.0.5")

    // QuickShop-API
    compileOnly("com.ghostchu:quickshop-api:6.2.0.5")

    // TriumphGui
    implementation("dev.triumphteam:triumph-gui:3.1.7") { exclude("net.kyori") }

    // Head Database
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.2")
}

indra {
    javaVersions().target(17)
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
    jar {
        archiveClassifier.set("noshade")
    }
    shadowJar {
        archiveFileName.set("${project.name}.jar") //-${project.version}
        sequenceOf(
            "io.papermc.lib",
            "io.leangen.geantyref",
            "com.github.KodySimpson",
            "dev.triumphteam"
        ).forEach {
            relocate(it, "io.myzticbean.finditemaddon.lib.$it")
        }
    }
    processResources {
        val tokens = mapOf(
            "project.version" to project.version
        )
        inputs.properties(tokens)
        filesMatching("**/*.yml") {
            // Some of our files are too large to use Groovy templating
            filter { string ->
                var result = string
                for ((key, value) in tokens) {
                    result = result.replace("\${$key}", value.toString())
                }
                result
            }
        }
    }
    compileJava {
        options.compilerArgs.add("-Xlint:-classfile,-processing")
    }
}

fun lastCommitHash(): String = indraGit.commit()?.name?.substring(0, 7)
    ?: error("Could not determine commit hash")

fun String.decorateVersion(): String = if (endsWith("-SNAPSHOT")) "$this+${lastCommitHash()}" else this
