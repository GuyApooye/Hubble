package com.github.guyapooye.hubble.client.shader.block;

import com.github.guyapooye.hubble.ext.VeilShaderBufferLayoutBuilderExtension;
import com.github.guyapooye.hubble.registry.HubbleShaderBufferRegistry;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilShaderBufferLayout;
import foundry.veil.api.client.render.shader.block.ShaderBlock;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@SuppressWarnings("unchecked")
public final class SunData {

    public static final int SIZE = 25;
    private Vector3f[] position = new Vector3f[SIZE];
    private Vector3f[] dimensions = new Vector3f[SIZE];
    private Matrix4f[] rotation = new Matrix4f[SIZE];
    private Vector3f[] color = new Vector3f[SIZE];
    private Float[] intensity = new Float[SIZE];
    private int length = 0;


    public SunData() {}

    public static VeilShaderBufferLayout<SunData> createLayout() {
        return ((VeilShaderBufferLayoutBuilderExtension<SunData>)((VeilShaderBufferLayoutBuilderExtension<SunData>)((VeilShaderBufferLayoutBuilderExtension<SunData>) ((VeilShaderBufferLayoutBuilderExtension<SunData>)((VeilShaderBufferLayoutBuilderExtension<SunData>)((VeilShaderBufferLayoutBuilderExtension<SunData>)
                VeilShaderBufferLayout.builder())
                .hubble$vec3s("Pos", SIZE, SunData::getPosition))
                .hubble$vec3s("Dims", SIZE, SunData::getDimensions))
                .hubble$mat4s("Rot", SIZE, SunData::getRotation))
                .hubble$vec3s("Color", SIZE, SunData::getColor))
                .hubble$f32s("Intensity", SIZE, SunData::getIntensity))
                .hubble$f32s("Size", SIZE, SunData::getSize)
                .integer("Length", SunData::getLength).build();
    }

    public Vector3f[] getPosition() {
        return position;
    }

    public Vector3f[] getDimensions() {
        return dimensions;
    }

    public Matrix4f[] getRotation() {
        return rotation;
    }

    public Vector3f[] getColor() {
        return color;
    }

    public Float[] getIntensity() {
        return intensity;
    }

    public Float[] getSize() {
        Float[] size = new Float[SIZE];
        for (int i = 0; i < this.length; i++) {
            size[i] = dimensions[i].length();
        }
        return size;
    }

    public Matrix4f[] getInvRot() {
        Matrix4f[] invRot = new Matrix4f[SIZE];
        for (int i = 0; i < rotation.length; i++) {
            if (rotation[i] == null) continue;
            invRot[i] = rotation[i].invert();
        }
        return invRot;
    }

    public int getLength() {
        return length;
    }

    private void setValues(Vector3f[] pos, Vector3f[] dims, Matrix4f[] rot, Vector3f[] color, Float[] intensity, int length) {
        ShaderBlock<SunData> block = VeilRenderSystem.getBlock(HubbleShaderBufferRegistry.LIGHT_DATA.get());
        if (block == null) throw new IllegalStateException("Sun data has not been initialized!");
        if (length >= SIZE) throw new RuntimeException("Size of sun data cannot be greater than max size ("+SIZE+")");
        if (pos.length > SIZE || dims.length > SIZE || rot.length > SIZE || color.length > SIZE || intensity.length > SIZE) throw new IllegalArgumentException("Incorrect data array size!");
        this.position = pos.clone();
        this.dimensions = dims.clone();
        this.rotation = rot.clone();
        this.color = color.clone();
        this.intensity = intensity.clone();
        this.length = length;
    }

    public void update() {
        ShaderBlock<SunData> block = VeilRenderSystem.getBlock(HubbleShaderBufferRegistry.LIGHT_DATA.get());
        if (block == null) throw new IllegalStateException("Sun data has not been initialized!");
        block.set(this);
        VeilRenderSystem.bind(HubbleShaderBufferRegistry.LIGHT_DATA.get());
    }

    public void backup(SunData store) {
        store.position = this.position.clone();
        store.dimensions = this.dimensions.clone();
        store.rotation = this.rotation.clone();
        store.color = this.color.clone();
        store.intensity = this.intensity.clone();
        store.length = length;
    }

    public void restore(SunData load) {
        ShaderBlock<SunData> block = VeilRenderSystem.getBlock((HubbleShaderBufferRegistry.LIGHT_DATA.get()));
        if (block == null) throw new IllegalStateException("Sun data has not been initialized!");
        this.position = load.position.clone();
        this.dimensions = load.dimensions.clone();
        this.rotation = load.rotation.clone();
        this.color = load.color.clone();
        this.intensity = load.intensity.clone();
        this.length = load.length;
        block.set(this);
        VeilRenderSystem.bind(HubbleShaderBufferRegistry.LIGHT_DATA.get());
    }

    public void clear() {
        setValues(new Vector3f[SIZE], new Vector3f[SIZE], new Matrix4f[SIZE], new Vector3f[SIZE], new Float[SIZE], 0);
    }

    public void addValues(Vector3f pos, Vector3f dims, Matrix4f rot, Vector3f color, float intensity) {
        if (length >= SIZE) throw new RuntimeException("Size of planet data cannot be greater than max size ("+SIZE+")");
        this.position[length] = pos;
        this.dimensions[length] = dims;
        this.rotation[length] = rot;
        this.color[length] = color;
        this.intensity[length] = intensity;
        length++;
    }
}
