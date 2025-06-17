package com.github.guyapooye.hubble.client.render.shader.block;

import com.github.guyapooye.hubble.ext.VeilShaderBufferLayoutBuilderExtension;
import com.github.guyapooye.hubble.registry.HubbleShaderBufferRegistry;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilShaderBufferLayout;
import foundry.veil.api.client.render.shader.block.ShaderBlock;
import org.joml.Vector3f;

@SuppressWarnings("unchecked")
public final class AtmosphereData {
    public static final int SIZE = PlanetData.SIZE;
    private Float[] densityFalloff = new Float[SIZE];
    private Float[] scale = new Float[SIZE];
    private Float[] strength = new Float[SIZE];
    private Vector3f[] scatteringCoefficients = new Vector3f[SIZE];
    private int inScatteringPoints = 4;
    private int opticalDepthPoints = 4;
    private int length = 0;

    public AtmosphereData() {}

    public static VeilShaderBufferLayout<AtmosphereData> createLayout() {
        return ((VeilShaderBufferLayoutBuilderExtension<AtmosphereData>)((VeilShaderBufferLayoutBuilderExtension<AtmosphereData>)((VeilShaderBufferLayoutBuilderExtension<AtmosphereData>) ((VeilShaderBufferLayoutBuilderExtension<AtmosphereData>)
                VeilShaderBufferLayout.builder())
                .hubble$f32s("DensityFalloff", SIZE, AtmosphereData::getDensityFalloff))
                .hubble$f32s("Scale", SIZE, AtmosphereData::getScale))
                .hubble$f32s("Strength", SIZE, AtmosphereData::getStrength))
                .hubble$vec3s("ScatteringCoefficients", SIZE, AtmosphereData::getScatteringCoefficients)
                .integer("InScatteringPoints", AtmosphereData::getInScatteringPoints)
                .integer("OpticalDepthPoints", AtmosphereData::getOpticalDepthPoints)
                .integer("Length", AtmosphereData::getLength)
                .build();
    }

    private void setValues(Float[] densityFalloff, Float[] scale, Float[] strength, int length) {
        ShaderBlock<PlanetData> block = VeilRenderSystem.getBlock(HubbleShaderBufferRegistry.PLANET_DATA.get());
        if (block == null) throw new IllegalStateException("Atmosphere data has not been initialized!");
        if (length >= SIZE) throw new RuntimeException("Size of atmosphere data cannot be greater than max size ("+SIZE+")");
        if (densityFalloff.length > SIZE || scale.length > SIZE || strength.length > SIZE) throw new IllegalArgumentException("Incorrect data array size!");
        this.densityFalloff = densityFalloff.clone();
        this.scale = scale.clone();
        this.strength = strength.clone();
        this.length = length;
    }

    public void update() {
        ShaderBlock<AtmosphereData> block = VeilRenderSystem.getBlock(HubbleShaderBufferRegistry.ATMOSPHERE_DATA.get());
        if (block == null) throw new IllegalStateException("Planet data has not been initialized!");
        block.set(this);
        VeilRenderSystem.bind(HubbleShaderBufferRegistry.ATMOSPHERE_DATA.get());
    }

    public void clear() {
        setValues(new Float[SIZE], new Float[SIZE], new Float[SIZE], 0);
    }

    public void backup(AtmosphereData store) {
        store.densityFalloff = this.densityFalloff.clone();
        store.scale = this.scale.clone();
        store.strength = this.strength.clone();
        store.scatteringCoefficients = this.scatteringCoefficients.clone();
        store.inScatteringPoints = this.inScatteringPoints;
        store.opticalDepthPoints = this.opticalDepthPoints;
        store.length = this.length;
    }

    public void restore(AtmosphereData load) {
        ShaderBlock<AtmosphereData> block = VeilRenderSystem.getBlock(HubbleShaderBufferRegistry.ATMOSPHERE_DATA.get());
        if (block == null) throw new IllegalStateException("Planet data has not been initialized!");
        this.densityFalloff = load.densityFalloff.clone();
        this.scale = load.scale.clone();
        this.strength = load.strength.clone();
        this.scatteringCoefficients = load.scatteringCoefficients.clone();
        this.inScatteringPoints = load.inScatteringPoints;
        this.opticalDepthPoints = load.opticalDepthPoints;
        this.length = load.length;
        block.set(this);
        VeilRenderSystem.bind(HubbleShaderBufferRegistry.ATMOSPHERE_DATA.get());
    }

    public Float[] getDensityFalloff() {
        return densityFalloff;
    }

    public Float[] getScale() {
        return scale;
    }

    public Float[] getStrength() {
        return strength;
    }

    public Vector3f[] getScatteringCoefficients() {
        return scatteringCoefficients;
    }

    public int getInScatteringPoints() {
        return inScatteringPoints;
    }

    public int getOpticalDepthPoints() {
        return opticalDepthPoints;
    }

    public int getLength() {
        return length;
    }

    public void setGlobalSettings(int inScatteringPoints, int opticalDepthPoints) {
        this.inScatteringPoints = inScatteringPoints;
        this.opticalDepthPoints = opticalDepthPoints;
    }

    public void addValues(Float densityFalloff, Float scale, Float strength, Vector3f scatteringCoefficients) {
        if (length >= SIZE) throw new RuntimeException("Size of planet data cannot be greater than max size ("+SIZE+")");
        this.densityFalloff[length] = densityFalloff;
        this.scale[length] = scale;
        this.strength[length] = strength;
        this.scatteringCoefficients[length] = scatteringCoefficients;
        length++;
    }


}
