package com.github.guyapooye.hubble.client.shader.block;

import com.github.guyapooye.hubble.ext.VeilShaderBufferLayoutBuilderExtension;
import com.github.guyapooye.hubble.registry.HubbleShaderBufferRegistry;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilShaderBufferLayout;
import foundry.veil.api.client.render.shader.block.ShaderBlock;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@SuppressWarnings("unchecked")
public class PlanetData {

    public static final int SIZE = 25;
    private Vector3f[] pos = new Vector3f[SIZE];
    private Vector3f[] dims = new Vector3f[SIZE];
    private Matrix4f[] rot = new Matrix4f[SIZE];
    private int size = 0;


    public PlanetData() {}

    public static VeilShaderBufferLayout<PlanetData> createLayout() {
        return ((VeilShaderBufferLayoutBuilderExtension<PlanetData>)((VeilShaderBufferLayoutBuilderExtension<PlanetData>)((VeilShaderBufferLayoutBuilderExtension<PlanetData>)((VeilShaderBufferLayoutBuilderExtension<PlanetData>)
                VeilShaderBufferLayout.builder()).hubble$vec3s("Pos", SIZE, PlanetData::getPos)).hubble$vec3s("Dims", SIZE, PlanetData::getDims)).hubble$mat4s("Rot", SIZE, PlanetData::getRot)).hubble$mat4s("InvRot", SIZE, PlanetData::getInvRot).integer("Size", PlanetData::getSize).build();
    }

    public boolean update(Vector3f[] pos, Vector3f[] dims, Matrix4f[] rot, int dataSize) {
        ShaderBlock<PlanetData> block = VeilRenderSystem.getBlock(HubbleShaderBufferRegistry.PLANET_DATA.get());
        if (block == null && dataSize >= SIZE) return false;
        this.pos = pos.clone();
        this.dims = dims.clone();
        this.rot = rot.clone();
        this.size = dataSize;
        block.set(this);
        VeilRenderSystem.bind(HubbleShaderBufferRegistry.PLANET_DATA.get());
        return true;
    }

    public boolean setValuesNoUpdate(Vector3f[] pos, Vector3f[] dims, Matrix4f[] rot, int dataSize) {
        ShaderBlock<PlanetData> block = VeilRenderSystem.getBlock(HubbleShaderBufferRegistry.PLANET_DATA.get());
        if (block == null && dataSize >= SIZE) return false;
        this.pos = pos.clone();
        this.dims = dims.clone();
        this.rot = rot.clone();
        this.size = dataSize;
        return true;
    }

    public void update() {
        ShaderBlock<PlanetData> block = VeilRenderSystem.getBlock(HubbleShaderBufferRegistry.PLANET_DATA.get());
        if (block != null) {
            block.set(this);
            VeilRenderSystem.bind(HubbleShaderBufferRegistry.PLANET_DATA.get());
        }
    }

    public void clear() {
        update(new Vector3f[SIZE], new Vector3f[SIZE], new Matrix4f[SIZE], 0);
    }

    public void clearNoUpdate() {
        setValuesNoUpdate(new Vector3f[SIZE], new Vector3f[SIZE], new Matrix4f[SIZE], 0);
    }

    public void backup(PlanetData store) {
        store.pos = this.pos.clone();
        store.dims = this.dims.clone();
        store.rot = this.rot.clone();
        store.size = this.size;
    }

    public void restore(PlanetData load) {
        ShaderBlock<PlanetData> block = VeilRenderSystem.getBlock(HubbleShaderBufferRegistry.PLANET_DATA.get());
        if (block != null) {
            this.pos = load.pos.clone();
            this.dims = load.dims.clone();
            this.rot = load.rot.clone();
            this.size = load.size;
            block.set(this);
            VeilRenderSystem.bind(HubbleShaderBufferRegistry.PLANET_DATA.get());
        }
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

    public boolean addValues(Vector3f pos, Vector3f dims, Matrix4f rot) {
        ShaderBlock<PlanetData> block = VeilRenderSystem.getBlock(HubbleShaderBufferRegistry.PLANET_DATA.get());
        if (block == null || size >= SIZE) return false;
        this.pos[size] = pos;
        this.dims[size] = dims;
        this.rot[size] = rot;
        size++;
        block.set(this);
        VeilRenderSystem.bind(HubbleShaderBufferRegistry.PLANET_DATA.get());
        return true;
    }

    public boolean addValuesNoUpdate(Vector3f pos, Vector3f dims, Matrix4f rot) {
        if (size >= SIZE) return false;
        this.pos[size] = pos;
        this.dims[size] = dims;
        this.rot[size] = rot;
        size++;
        return true;
    }
}
