package crystal0404.legacyraid.fabric;

import crystal0404.legacyraid.LegacyRaid;
import net.fabricmc.api.ModInitializer;

public class LegacyRaidFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LegacyRaid.init();
    }
}
