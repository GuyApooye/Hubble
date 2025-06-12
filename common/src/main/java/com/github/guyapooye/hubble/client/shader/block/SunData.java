package com.github.guyapooye.hubble.client.shader.block;

import com.github.guyapooye.hubble.ext.VeilShaderBufferLayoutBuilderExtension;
import com.github.guyapooye.hubble.registry.HubbleShaderBufferRegistry;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilShaderBufferLayout;
import foundry.veil.api.client.render.shader.block.ShaderBlock;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@SuppressWarnings("unchecked")
public class SunData {

    public static final int SIZE = 25;
    private Vector3f[] pos = new Vector3f[SIZE];
    private Vector3f[] dims = new Vector3f[SIZE];
    private Matrix4f[] rot = new Matrix4f[SIZE];
    private Vector3f[] color = new Vector3f[SIZE];
    private Float[] intensity = new Float[SIZE];
    private int size = 0;


    public SunData() {}

    public static VeilShaderBufferLayout<SunData> createLayout() {
        return ((VeilShaderBufferLayoutBuilderExtension<SunData>)((VeilShaderBufferLayoutBuilderExtension<SunData>)((VeilShaderBufferLayoutBuilderExtension<SunData>) ((VeilShaderBufferLayoutBuilderExtension<SunData>)((VeilShaderBufferLayoutBuilderExtension<SunData>)((VeilShaderBufferLayoutBuilderExtension<SunData>)
                VeilShaderBufferLayout.builder()).hubble$vec3s("Pos", SIZE, SunData::getPos)).hubble$vec3s("Dims", SIZE, SunData::getDims)).hubble$mat4s("Rot", SIZE, SunData::getRot)).hubble$vec3s("Color", SIZE, SunData::getColor)).hubble$f32s("Intensity", SIZE, SunData::getIntensity)).hubble$f32s("Length", SIZE, SunData::getLength).integer("Size", SunData::getSize).build();
    }

    public Vector3f[] getPos() {
        return pos;
    }

    public Vector3f[] getDims() {
        return dims;
    }

    public Matrix4f[] getRot() {
        return rot;
    }

    public Vector3f[] getColor() {
        return color;
    }

    public Float[] getIntensity() {
        return intensity;
    }

    public Float[] getLength() {
        Float[] size = new Float[SIZE];
        for (int i = 0; i < this.size; i++) {
            size[i] = dims[i].length();
        }
        return size;
    }

    public Matrix4f[] getInvRot() {
        Matrix4f[] invRot = new Matrix4f[SIZE];
        for (int i = 0; i < rot.length; i++) {
            if (rot[i] == null) continue;
            invRot[i] = rot[i].invert();
        }
        return invRot;
    }

    public int getSize() {
        return size;
    }

    private void setValues(Vector3f[] pos, Vector3f[] dims, Matrix4f[] rot, Vector3f[] color, Float[] intensity, int size) {
        ShaderBlock<SunData> block = VeilRenderSystem.getBlock(HubbleShaderBufferRegistry.LIGHT_DATA.get());
        if (block == null) throw new IllegalStateException("Sun data has not been initialized!");
        if (size >= SIZE) throw new RuntimeException("Size of sun data cannot be greater than max size ("+SIZE+")");
        this.pos = pos.clone();
        this.dims = dims.clone();
        this.rot = rot.clone();
        this.color = color.clone();
        this.intensity = intensity.clone();
        this.size = size;
    }

    public void update() {
        ShaderBlock<SunData> block = VeilRenderSystem.getBlock(HubbleShaderBufferRegistry.LIGHT_DATA.get());
        if (block == null) throw new IllegalStateException("Sun data has not been initialized!");
        block.set(this);
        VeilRenderSystem.bind(HubbleShaderBufferRegistry.LIGHT_DATA.get());
    }

    public void backup(SunData store) {
        store.pos = this.pos.clone();
        store.dims = this.dims.clone();
        store.rot = this.rot.clone();
        store.color = this.color.clone();
        store.intensity = this.intensity.clone();
        store.size = size;
    }

    public void restore(SunData load) {
        ShaderBlock<SunData> block = VeilRenderSystem.getBlock((HubbleShaderBufferRegistry.LIGHT_DATA.get()));
        if (block == null) throw new IllegalStateException("Sun data has not been initialized!");
        this.pos = load.pos.clone();
        this.dims = load.dims.clone();
        this.rot = load.rot.clone();
        this.color = load.color.clone();
        this.intensity = load.intensity.clone();
        this.size = load.size;
        block.set(this);
        VeilRenderSystem.bind(HubbleShaderBufferRegistry.LIGHT_DATA.get());
    }

    public void clear() {
        setValues(new Vector3f[SIZE], new Vector3f[SIZE], new Matrix4f[SIZE], new Vector3f[SIZE], new Float[SIZE], 0);
    }

    public void addValues(Vector3f pos, Vector3f dims, Matrix4f rot, Vector3f color, float intensity) {
        if (size >= SIZE) throw new RuntimeException("Size of planet data cannot be greater than max size ("+SIZE+")");
        this.pos[size] = pos;
        this.dims[size] = dims;
        this.rot[size] = rot;
        this.color[size] = color;
        this.intensity[size] = intensity;
        size++;
    }
}
