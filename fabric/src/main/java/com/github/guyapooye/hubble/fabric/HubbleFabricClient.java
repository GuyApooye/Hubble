package com.github.guyapooye.hubble.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import static com.github.guyapooye.hubble.HubbleClient.*;

public final class HubbleFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        initClient();
        KeyBindingHelper.registerKeyBinding(ROLL.get());
        KeyBindingHelper.registerKeyBinding(ROLL_INVERSE.get());
    }
}
