package com.github.guyapooye.hubble.neoforge;

import com.github.guyapooye.hubble.HubbleClient;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.jarjar.nio.util.Lazy;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import static com.github.guyapooye.hubble.Hubble.MOD_ID;
import static com.github.guyapooye.hubble.HubbleClient.*;

@Mod(value = MOD_ID, dist = Dist.CLIENT)
public final class HubbleNeoForgeClient {

    public HubbleNeoForgeClient(IEventBus modEventBus) {
        modEventBus.register(this);
        initClient();
    }

    @SubscribeEvent
    public void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(ROLL.get());
        event.register(ROLL_INVERSE.get());
    }

    public void onClientTick(ClientTickEvent.Post event) {
        while (ROLL.get().isDown()) {
            rollKeyBindPressed(false);
        }

        while (ROLL_INVERSE.get().isDown()) {
            rollKeyBindPressed(true);
        }
    }
}
