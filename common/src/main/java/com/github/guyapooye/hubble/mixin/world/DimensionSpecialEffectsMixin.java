package com.github.guyapooye.hubble.mixin.world;

import com.github.guyapooye.hubble.Hubble;
import com.github.guyapooye.hubble.client.world.SpaceEffects;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DimensionSpecialEffects.class)
public class DimensionSpecialEffectsMixin {
    @Shadow @Final private static Object2ObjectMap<ResourceLocation, DimensionSpecialEffects> EFFECTS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addSpecialEffects(CallbackInfo ci) {
        EFFECTS.put(Hubble.SPACE, new SpaceEffects());
    }
}
