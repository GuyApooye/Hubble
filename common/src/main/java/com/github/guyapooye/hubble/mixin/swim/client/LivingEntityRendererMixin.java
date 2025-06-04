package com.github.guyapooye.hubble.mixin.swim.client;

import com.github.guyapooye.hubble.HubbleUtil;
import com.github.guyapooye.hubble.ext.EntityExtension;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntityRenderer.class, priority = 1100)
public class LivingEntityRendererMixin {
    @ModifyExpressionValue(method = "setupRotations", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hasPose(Lnet/minecraft/world/entity/Pose;)Z", ordinal = 0))
    private <T extends LivingEntity> boolean disableRotate(boolean original, T livingEntity) {
        return original || (((EntityExtension)livingEntity).hubble$canSpaceWalk() && HubbleUtil.shouldSwimInSpace(livingEntity.level().dimension()));
    }

    @Inject(method = "setupRotations", at = @At("TAIL"))
    private <T extends LivingEntity> void rotatePoseInSpace(T livingEntity, PoseStack poseStack, float f, float g, float h, float i, CallbackInfo ci) {
        if ((((EntityExtension)livingEntity).hubble$canSpaceWalk() && HubbleUtil.shouldSwimInSpace(livingEntity.level().dimension()))) poseStack.mulPose(((EntityExtension) livingEntity).hubble$getViewRotation(h));
    }
}
