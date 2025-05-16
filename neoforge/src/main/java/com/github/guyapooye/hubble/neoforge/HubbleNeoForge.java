package com.github.guyapooye.hubble.neoforge;

import com.github.guyapooye.hubble.Hubble;
import net.neoforged.fml.common.Mod;

@Mod(Hubble.MOD_ID)
public final class HubbleNeoForge {
    public HubbleNeoForge() {
        // Run our common setup.
        Hubble.init();
    }
}
