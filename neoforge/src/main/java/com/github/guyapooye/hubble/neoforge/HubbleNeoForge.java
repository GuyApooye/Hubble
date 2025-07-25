package com.github.guyapooye.hubble.neoforge;

import com.github.guyapooye.hubble.Hubble;
import com.github.guyapooye.hubble.neoforge.event.HubbleNeoForgeEventHooks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

import static com.github.guyapooye.hubble.Hubble.MOD_ID;

@Mod(MOD_ID)
public final class HubbleNeoForge {

    public HubbleNeoForge(IEventBus modEventBus) {
        Hubble.init();
        HubbleNeoForgeEventHooks.register(modEventBus);
    }


}
