package com.github.guyapooye.hubble.fabric;

import com.github.guyapooye.hubble.Hubble;
import com.github.guyapooye.hubble.fabric.event.HubbleFabricEventHooks;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public final class HubbleFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Hubble.init();
        HubbleFabricEventHooks.register();

        Registry.register(BuiltInRegistries.BLOCK, Hubble.path("debug_reentry_block"), Hubble.DEBUG_REENTRY_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, Hubble.path("debug_reentry_block"), new BlockItem(Hubble.DEBUG_REENTRY_BLOCK, new Item.Properties()));
    }
}
