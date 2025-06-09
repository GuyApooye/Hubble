package com.github.guyapooye.hubble.api.effects;

import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class AtmosphereSettings {
    private Vector3f waveLengths;
    private float density;
    private float radius;

    public void update(AtmosphereSettings other, float partialTicks) {
        waveLengths.lerp(other.waveLengths, partialTicks);
        density = Mth.lerp(density, other.density, partialTicks);
        radius = Mth.lerp(radius, other.radius, partialTicks);
    }

    public void load(AtmosphereSettings other) {
        waveLengths.set(other.waveLengths);
        density = other.density;
        radius = other.radius;
    }

    public Vector3f getWaveLengths() {
        return waveLengths;
    }

    public float getDensity() {
        return density;
    }

    public float getRadius() {
        return radius;
    }
}
