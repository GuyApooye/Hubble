package com.github.guyapooye.hubble.mixin.veil.client;

import foundry.veil.api.client.render.shader.processor.ShaderBindingProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(value = ShaderBindingProcessor.class, remap = false)
public class VeilShaderBindingProcessorMixin {
    @Redirect(method = "lambda$modify$0", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private static <E> Iterator<E> copyThenIterate(List<E> instance) {
        return new ArrayList<>(instance).iterator();
    }
}
