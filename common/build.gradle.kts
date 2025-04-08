architectury {
    common(rootProject.property("enabled_platforms").toString().split(','))
}

dependencies {
    // We depend on Fabric Loader here to use the Fabric @Environment annotations,
    // which get remapped to the correct annotations on each platform.
    // Do NOT use other classes from Fabric Loader.
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
}

loom {
    accessWidenerPath.set(file("src/main/resources/legacyraid.accesswidener"))

    mixin {
       useLegacyMixinAp.set(false)
    }
}