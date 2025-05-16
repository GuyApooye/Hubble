package com.github.guyapooye.hubble.mixin.debug;

import foundry.veil.api.client.render.VeilShaderBufferLayout;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.impl.client.render.pipeline.VeilShaderBufferCache;
import foundry.veil.impl.client.render.shader.block.LayoutShaderBlockImpl;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = VeilShaderBufferCache.class, remap = false)
public class DebugVeilShaderBufferCacheMixin {
    @Shadow private VeilShaderBufferLayout<?>[] layouts;

    @Shadow private LayoutShaderBlockImpl<?>[] values;

    @Inject(method = "onShaderCompile", at = @At("TAIL"))
    private void debugCountBuffers(Map<ResourceLocation, ShaderProgram> updatedPrograms, CallbackInfo ci) {
        System.out.println("LAYOUTS: " + layouts.length);
        System.out.println("VALUES: " + values.length);
    }
}
