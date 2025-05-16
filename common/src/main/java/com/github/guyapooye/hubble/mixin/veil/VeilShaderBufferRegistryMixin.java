package com.github.guyapooye.hubble.mixin.veil;

import com.github.guyapooye.hubble.registry.HubbleShaderBufferRegistry;
import foundry.veil.api.client.registry.VeilShaderBufferRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VeilShaderBufferRegistry.class, remap = false)
public abstract class VeilShaderBufferRegistryMixin {

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void hubble$register(CallbackInfo ci) {
        HubbleShaderBufferRegistry.bootstrap();
    }
}
