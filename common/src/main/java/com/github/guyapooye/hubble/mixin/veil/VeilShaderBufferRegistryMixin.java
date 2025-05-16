package com.github.guyapooye.hubble.mixin.veil;


import com.github.guyapooye.hubble.client.render.shader.block.LightData;
import com.github.guyapooye.hubble.client.render.shader.block.PlanetData;
import foundry.veil.api.client.registry.VeilShaderBufferRegistry;
import foundry.veil.api.client.render.VeilShaderBufferLayout;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(value = VeilShaderBufferRegistry.class, remap = false)
public abstract class VeilShaderBufferRegistryMixin {

    @Shadow
    protected static <T> Supplier<VeilShaderBufferLayout<T>> register(String name, Supplier<VeilShaderBufferLayout<T>> layout) {
        return null;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addOwn(CallbackInfo ci) {
        register("planet_data", PlanetData::createLayout);
        register("light_data", LightData::createLayout);
    }
}
