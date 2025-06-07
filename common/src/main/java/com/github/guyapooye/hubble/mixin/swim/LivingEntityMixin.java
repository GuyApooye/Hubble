package com.github.guyapooye.hubble.mixin.swim;

import com.github.guyapooye.hubble.ext.EntityExtension;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class, priority = 900)
public abstract class LivingEntityMixin implements EntityExtension {
    @Inject(method = "goDownInWater", at = @At("HEAD"), cancellable = true)
    private void disableGoDownInWaterInSpace(CallbackInfo ci) {
        if (hubble$canSpaceWalk()) ci.cancel();
    }
}
