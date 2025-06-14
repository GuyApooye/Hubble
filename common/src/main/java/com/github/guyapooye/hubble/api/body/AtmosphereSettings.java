package com.github.guyapooye.hubble.api.body;

import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class AtmosphereSettings {
    private float densityFalloff = 2.0f;
    private float size = 0.3f;
    private float strength = 0.3f;
    private Vector3f scatteringCoefficients = new Vector3f();

    public AtmosphereSettings() {

    }

    public float getDensityFalloff() {
        return densityFalloff;
    }

    public float getSize() {
        return size;
    }

    public float getStrength() {
        return strength;
    }

    public Vector3f getScatteringCoefficients() {
        return scatteringCoefficients;
    }

    public AtmosphereSettings(float densityFalloff, float size, float strength, Vector3f scatteringCoefficients) {
        this.densityFalloff = densityFalloff;
        this.size = size;
        this.strength = strength;
        this.scatteringCoefficients = scatteringCoefficients;
    }

    public void update(AtmosphereSettings load, float partialTicks) {
        densityFalloff = Mth.lerp(partialTicks, densityFalloff, load.densityFalloff);
        size = Mth.lerp(partialTicks, size, load.size);
        strength = Mth.lerp(partialTicks, strength, load.strength);
        scatteringCoefficients.lerp(load.scatteringCoefficients, partialTicks);
    }

    public void load(AtmosphereSettings load) {
        densityFalloff = load.densityFalloff;
        size = load.size;
        strength = load.strength;
        scatteringCoefficients.set(scatteringCoefficients);
    }
}
