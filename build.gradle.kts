import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("fabric-loom") version "1.12-SNAPSHOT"
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm") version "2.2.21"
}

val modVersion : String by project
val modGroupId : String by project
val modId : String by project
val modName : String by project

val minecraftVersion : String by project
val loaderVersion : String by project
val fabricVersion : String by project
val fabricKotlinVersion : String by project
val modMenuVersion : String by project
val emiVersion : String by project

version = modVersion
group = modGroupId

base {
    archivesName.set(modName)
}

repositories {
    maven {
        name = "Terraformers"
        url = uri("https://maven.terraformersmc.com/")
    }
    maven {
        name = "EMI"
        url = uri("https://maven.terraformersmc.com/releases")
    }
}

fabricApi {
    configureDataGeneration {
        client.set(true)
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${loaderVersion}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${fabricKotlinVersion}")
    modLocalRuntime("com.terraformersmc:modmenu:${modMenuVersion}")
    modLocalRuntime("dev.emi:emi-fabric:$emiVersion")
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to inputs.properties["version"])
    }
}

tasks.withType<KotlinCompile>().all {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
    inputs.property("archivesName", project.base.archivesName.get())

    from("LICENSE") {
        rename { "${it}_${inputs.properties["archivesName"]}" }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = modId
            from(components["java"])
        }
    }

    repositories {
    }
}

