plugins {
    id("com.github.johnrengelman.shadow")
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

configurations {
    create("common") {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
    getByName("compileClasspath").extendsFrom(getByName("common"))
    getByName("runtimeClasspath").extendsFrom(getByName("common"))
    getByName("developmentNeoForge").extendsFrom(getByName("common"))

    // Files in this configuration will be bundled into your mod using the Shadow plugin.
    // Don't use the `shadow` configuration from the plugin itself as it's meant for excluding files.
    create("shadowBundle") {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
}

repositories {
    maven {
        name = "NeoForged"
        url = uri("https://maven.neoforged.net/releases")
    }
}

dependencies {
    "neoForge"("net.neoforged:neoforge:${rootProject.property("neoforge_version")}")

    "common"(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    "shadowBundle"(project(":common", configuration = "transformProductionNeoForge"))
}

loom {
    accessWidenerPath.set(file("../common/src/main/resources/legacyraid.accesswidener"))

    mixin {
        useLegacyMixinAp.set(false)
    }
}

tasks.remapJar {
    atAccessWideners.add("legacyraid.accesswidener")
}

tasks.sourcesJar {
    from(project(":common").sourceSets["main"].allSource)
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand("version" to project.version)
    }
}

tasks.shadowJar {
    configurations = listOf(project.configurations.getByName("shadowBundle"))
    archiveClassifier.set("dev-shadow")
}

tasks.remapJar {
    inputFile.set(tasks.shadowJar.get().archiveFile)
}

publishMods {
    file = tasks.remapJar.get().archiveFile
    displayName = "[NeoForge]LegacyRaid ${rootProject.property("mod_version")} for Minecraft ${rootProject.property("minecraft_version")}"
    version = "v${rootProject.property("mod_version")}-mc${rootProject.property("minecraft_version")}-neoforge"
    changelog = providers.environmentVariable("CHANGELOG")
    val version = rootProject.property("mod_version") as String
    type = when {
        version.endsWith("beta") -> BETA
        version.endsWith("alpha") -> ALPHA
        else -> STABLE
    }
    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        projectId = "A4m2Hcnr"
        minecraftVersionRange {
            start = rootProject.property("start").toString()
            end = rootProject.property("end").toString()
        }
        modLoaders.add("neoforge")
        incompatible {
            // Crystal Carpet Addition
            id = "G26sLP13"
        }
        incompatible {
            // Raid Restorer
            id = "7YpmyzZr"
        }
    }
}