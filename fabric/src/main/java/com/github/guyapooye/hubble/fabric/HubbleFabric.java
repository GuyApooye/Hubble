package com.github.guyapooye.hubble.fabric;

import com.github.guyapooye.hubble.Hubble;
import com.github.guyapooye.hubble.fabric.event.HubbleFabricEventHooks;
import net.fabricmc.api.ModInitializer;

public final class HubbleFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Hubble.init();
        HubbleFabricEventHooks.register();
    }
}
