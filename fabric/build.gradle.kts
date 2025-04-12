plugins {
    id("com.github.johnrengelman.shadow")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

configurations {
    create("common") {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
    getByName("compileClasspath").extendsFrom(getByName("common"))
    getByName("runtimeClasspath").extendsFrom(getByName("common"))
    getByName("developmentFabric").extendsFrom(getByName("common"))

    create("shadowBundle") {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
}

repositories {
    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }
    maven {
        name = "Terraformers"
        url = uri("https://maven.terraformersmc.com/")
    }
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")

    "common"(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    "shadowBundle"(project(":common", configuration = "transformProductionFabric"))

    // modmenu
    modRuntimeOnly("com.terraformersmc:modmenu:${rootProject.property("modmenu_version")}")

    val apiModules = setOf(
        "fabric-resource-loader-v0",
        "fabric-screen-api-v1",
        "fabric-key-binding-api-v1",
        "fabric-lifecycle-events-v1"
    )

    apiModules.forEach {
        modRuntimeOnly(fabricApi.module(it, "${rootProject.property("fabric_api_version")}"))
    }
}

loom {
    accessWidenerPath.set(file("../common/src/main/resources/legacyraid.accesswidener"))

    mixin {
        useLegacyMixinAp.set(false)
    }
}

tasks.remapJar {
    injectAccessWidener.set(true)
}

tasks.sourcesJar {
    from(project(":common").sourceSets["main"].allSource)
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
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
    displayName = "[Fabric]LegacyRaid ${rootProject.property("mod_version")} for Minecraft ${rootProject.property("minecraft_version")}"
    version = "v${rootProject.property("mod_version")}-mc${rootProject.property("minecraft_version")}-fabric"
    changelog = providers.environmentVariable("CHANGELOG")
    val version = rootProject.property("mod_version") as String
    type = when {
        version.endsWith("beta") -> BETA
        version.endsWith("alpha") -> ALPHA
        else -> STABLE
    }
    val mrOptions = modrinthOptions {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        projectId = "A4m2Hcnr"
        minecraftVersionRange {
            start = rootProject.property("start").toString()
            end = rootProject.property("end").toString()
        }
        incompatible {
            // Crystal Carpet Addition
            id = "G26sLP13"
            // Raid Restorer
            id = "7YpmyzZr"
        }
    }
    modrinth {
        from(mrOptions)
        modLoaders.add("fabric")
    }
}