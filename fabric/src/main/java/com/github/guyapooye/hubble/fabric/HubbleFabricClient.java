package com.github.guyapooye.hubble.fabric.client;

import net.fabricmc.api.ClientModInitializer;

import static com.github.guyapooye.hubble.Hubble.initClient;

public final class HubbleFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        initClient();
    }
}
