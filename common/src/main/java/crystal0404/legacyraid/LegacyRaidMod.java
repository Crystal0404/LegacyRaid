package crystal0404.legacyraid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LegacyRaidMod {
    public static final String MOD_ID = "legacyraid";
    public static final String MOD_NAME = "Legacy Raid";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static void init() {
        LOGGER.info("legacy raid has been loaded!");
    }
}
