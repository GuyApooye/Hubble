package com.github.guyapooye.hubble.fabric;

import net.fabricmc.api.ClientModInitializer;

import static com.github.guyapooye.hubble.HubbleClient.initClient;

public final class HubbleFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        initClient();
    }
}
