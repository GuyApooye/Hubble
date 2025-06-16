package com.github.guyapooye.hubble.fabric;

import com.github.guyapooye.hubble.Hubble;
import com.github.guyapooye.hubble.client.rendertype.HubbleRenderType;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.renderer.RenderType;

import static com.github.guyapooye.hubble.HubbleClient.*;

public final class HubbleFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        initClient();
        KeyBindingHelper.registerKeyBinding(ROLL.get());
        KeyBindingHelper.registerKeyBinding(ROLL_INVERSE.get());



        VeilEventPlatform.INSTANCE.onVeilRegisterBlockLayers(registry -> {
            registry.registerBlockLayer(VeilRenderType.get(Hubble.path("debug/reentry")));
        });
    }

    public static void registerBlockRenderLayers() {
        BlockRenderLayerMap.INSTANCE.putBlock(Hubble.DEBUG_REENTRY_BLOCK, VeilRenderType.get(Hubble.path("debug/reentry")));
    }
}
