package com.github.guyapooye.hubble.mixin.debug.client;

import com.mojang.blaze3d.platform.NativeImage;
import foundry.veil.api.client.render.texture.SimpleArrayTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SimpleArrayTexture.class)
public class VeilSimpleArrayTextureMixin {

//    @Redirect(method = "lambda$load$1", at = @At(value = "INVOKE", target = "Lfoundry/veil/api/client/render/texture/SimpleArrayTexture;setFilter(ZZ)V"))
//    private void disableTest0(SimpleArrayTexture instance, boolean b1, boolean b2) {
//
//    }

//    @Redirect(method = "lambda$load$1", at = @At(value = "INVOKE", target = "Lfoundry/veil/api/client/render/texture/SimpleArrayTexture;init(IIIII)V"))
//    private void disableTest1(SimpleArrayTexture instance, int i1, int i2, int i3, int i4, int i5) {
//
//    }

//    @Redirect(method = "lambda$load$1", at = @At(value = "INVOKE", target = "Lfoundry/veil/api/client/render/texture/SimpleArrayTexture;upload([Lcom/mojang/blaze3d/platform/NativeImage;)V"))
//    private void disableTest0(SimpleArrayTexture instance, NativeImage[] nativeImages) {
//
//    }

}
