package com.github.guyapooye.hubble.mixin.swim.client;

import com.github.guyapooye.hubble.HubbleClient;
import com.github.guyapooye.hubble.ext.EntityExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin implements EntityExtension {
    @Inject(method = "aiStep", at = @At("HEAD"))
    private void induceRoll(CallbackInfo ci) {
        float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        if (HubbleClient.ROLL.get().consumeClick()) {
            hubble$getRotation().rotateZ(-0.2f*partialTicks);
        }
        if (HubbleClient.ROLL_INVERSE.get().consumeClick()) {
            hubble$getRotation().rotateZ(0.2f*partialTicks);
        }
    }
}
