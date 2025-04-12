import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("dev.architectury.loom").version("1.10-SNAPSHOT").apply(false)
    id("architectury-plugin").version("3.4-SNAPSHOT")
    id("me.modmuss50.mod-publish-plugin").version("0.8.4")
    id("com.github.johnrengelman.shadow").version("8.1.1").apply(false)
    id("java")
    id("maven-publish")
}

architectury {
    minecraft = project.property("minecraft_version") as String
}

allprojects {
    fun getVersion(): String {
        var version = rootProject.property("mod_version") as String
        if (System.getenv("BUILD_RELEASE") != "true" && System.getenv("JITPACK") != "true") {
            val buildNumber = System.getenv("GITHUB_RUN_NUMBER")
            version += if (buildNumber != null) ("+build.$buildNumber") else "-SNAPSHOT"
        }
        return version
    }

    group = rootProject.property("maven_group") as String
    version = getVersion()
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "architectury-plugin")
    apply(plugin = "me.modmuss50.mod-publish-plugin")
    apply(plugin = "maven-publish")

    configure<BasePluginExtension> {
        archivesName.set("${rootProject.property("archives_name")}-${project.name}")
    }

    repositories {

    }

    dependencies {
        val loom = project.extensions.getByName("loom") as LoomGradleExtensionAPI

        "minecraft"("net.minecraft:minecraft:${rootProject.property("minecraft_version")}")
        "mappings"(loom.layered {
            mappings("net.fabricmc:yarn:${rootProject.property("yarn_mappings")}:v2")
            mappings("dev.architectury:yarn-mappings-patch-neoforge:${rootProject.property("yarn_mappings_patch_neoforge_version")}")
        })
    }

    java {
        // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
        // if it is present.
        // If you remove this line, sources will not be generated.
        withSourcesJar()

        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    tasks.withType<JavaCompile>().configureEach {
        options.release.set(21)
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifactId = base.archivesName.get()
                from(components["java"])
            }
        }

        // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
        repositories {
            // Add repositories to publish to here.
            // Notice: This block does NOT have the same function as the block in the top level.
            // The repositories here will be used for publishing your artifact, not for
            // retrieving dependencies.
        }
    }
}

publishMods {
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

    modrinth("modrinthFabric") {
        from(mrOptions)
        file(project(":fabric"))
        modLoaders.add("fabric")
    }

    modrinth("modrinthNeoForge") {
        from(mrOptions)
        file(project(":neoforge"))
        modLoaders.add("neoforge")
    }
}