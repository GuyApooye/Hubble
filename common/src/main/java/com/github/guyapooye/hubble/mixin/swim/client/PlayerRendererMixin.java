package com.github.guyapooye.hubble.mixin.swim.client;

import com.github.guyapooye.hubble.HubbleUtil;
import com.github.guyapooye.hubble.ext.EntityExtension;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PlayerRenderer.class, priority = 1100)
public class PlayerRendererMixin {
    @WrapOperation(method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F"))
    private float dontRotateIfSpace(float f, float g, float h, Operation<Float> original, AbstractClientPlayer abstractClientPlayer) {
        if (((EntityExtension)abstractClientPlayer).hubble$canSpaceWalk() && HubbleUtil.shouldSwimInSpace(abstractClientPlayer.clientLevel.dimension())) return -90.0f;
        return original.call(f, g, h);
    }
}
