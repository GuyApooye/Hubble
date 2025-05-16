package com.github.guyapooye.hubble.client.world;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static java.lang.Float.NaN;

public final class SpaceEffects extends DimensionSpecialEffects {

    public SpaceEffects() {
        super(NaN, false, SkyType.NONE, false, false);
    }

    public @NotNull Vec3 getBrightnessDependentFogColor(Vec3 vec3, float f) {
        return new Vec3(0.0,0.0,0.0);
    }

    public boolean isFoggyAt(int i, int j) {
        return false;
    }
}
