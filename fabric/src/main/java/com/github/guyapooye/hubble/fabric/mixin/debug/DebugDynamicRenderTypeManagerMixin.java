package com.github.guyapooye.hubble.fabric.mixin.debug;

import com.github.guyapooye.hubble.fabric.HubbleFabric;
import com.github.guyapooye.hubble.fabric.HubbleFabricClient;
import foundry.veil.impl.client.render.rendertype.DynamicRenderTypeManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(DynamicRenderTypeManager.class)
public class DebugDynamicRenderTypeManagerMixin {
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("TAIL"))
    private void registerBlockRenderLayers(Map<ResourceLocation, byte[]> fileData, ResourceManager resourceManager, ProfilerFiller profilerFiller, CallbackInfo ci) {
        HubbleFabricClient.registerBlockRenderLayers();
    }
}
