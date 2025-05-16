package com.github.guyapooye.hubble.fabric;

import com.github.guyapooye.hubble.Hubble;
import net.fabricmc.api.ModInitializer;

public final class HubbleFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.

        Hubble.init();
    }
}
