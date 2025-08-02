package top.shenjack;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeirdoWorldGen implements ModInitializer {
    public static final String MOD_ID = "weirdo-world-gen";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // 是否开启完全 malloc
    public static boolean fullyMalloc = false;

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        LOGGER.info("HELLO from WeirdoWorldGen");
        LOGGER.warn("WARNING: you have installed WeirdoWorldGen, the world gen will be WEIRD");
    }
}