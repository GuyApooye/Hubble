package com.github.guyapooye.hubble.neoforge.client;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import static com.github.guyapooye.hubble.Hubble.MOD_ID;
import static com.github.guyapooye.hubble.Hubble.initClient;

@EventBusSubscriber(modid = MOD_ID)
public class HubbleNeoForgeClient {
    @SubscribeEvent
    public static void onClientInit(FMLClientSetupEvent setupClient) {
        initClient();
    }
}
