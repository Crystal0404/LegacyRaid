package crystal0404.legacyraid.fabric;

import net.fabricmc.api.ModInitializer;

import crystal0404.legacyraid.LegacyRaidMod;

public final class LegacyRaidFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LegacyRaidMod.init();
    }
}
