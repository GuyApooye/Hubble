package com.github.guyapooye.hubble.neoforge;

import com.github.guyapooye.hubble.Hubble;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

import static com.github.guyapooye.hubble.Hubble.MOD_ID;

@Mod(MOD_ID)
public final class HubbleNeoForge {
    public HubbleNeoForge() {
        // Run our common setup.
        NeoForge.EVENT_BUS.register(HubbleNeoForgeClient.class);
        Hubble.init();
    }


}
