package com.github.guyapooye.hubble.client.shader.block;

import com.github.guyapooye.hubble.ext.VeilShaderBufferLayoutBuilderExtension;
import com.github.guyapooye.hubble.registry.HubbleShaderBufferRegistry;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilShaderBufferLayout;
import foundry.veil.api.client.render.shader.block.ShaderBlock;
import foundry.veil.api.client.render.texture.SimpleArrayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@SuppressWarnings("unchecked")
public class PlanetData {

    public static final int SIZE = 25;
    private Vector3f[] position = new Vector3f[SIZE];
    private Vector3f[] dimensions = new Vector3f[SIZE];
    private Matrix4f[] rotation = new Matrix4f[SIZE];
    private ResourceLocation[] textures = new ResourceLocation[SIZE];
    private int size = 0;

    public PlanetData() {}

    public static VeilShaderBufferLayout<PlanetData> createLayout() {
        return (((VeilShaderBufferLayoutBuilderExtension<PlanetData>)((VeilShaderBufferLayoutBuilderExtension<PlanetData>)((VeilShaderBufferLayoutBuilderExtension<PlanetData>)
                VeilShaderBufferLayout.builder()).hubble$vec3s("Pos", SIZE, PlanetData::getPosition)).hubble$vec3s("Dims", SIZE, PlanetData::getDimensions)).hubble$mat4s("Rot", SIZE, PlanetData::getRotation).integer("Size", PlanetData::getSize)).build();
    }

    private void setValues(Vector3f[] pos, Vector3f[] dims, Matrix4f[] rot, ResourceLocation[] textures, int dataSize) {
        ShaderBlock<PlanetData> block = VeilRenderSystem.getBlock(HubbleShaderBufferRegistry.PLANET_DATA.get());
        if (block == null) throw new IllegalStateException("Planet data has not been initialized!");
        if (size >= SIZE) throw new RuntimeException("Size of planet data cannot be greater than max size ("+SIZE+")");
        this.position = pos.clone();
        this.dimensions = dims.clone();
        this.rotation = rot.clone();
        this.textures = textures.clone();
        this.size = dataSize;
    }

    public void update() {
        ShaderBlock<PlanetData> block = VeilRenderSystem.getBlock(HubbleShaderBufferRegistry.PLANET_DATA.get());
        if (block == null) throw new IllegalStateException("Planet data has not been initialized!");
        block.set(this);
        VeilRenderSystem.bind(HubbleShaderBufferRegistry.PLANET_DATA.get());
    }

    public void clear() {
        setValues(new Vector3f[SIZE], new Vector3f[SIZE], new Matrix4f[SIZE], new ResourceLocation[SIZE], 0);
    }

    public void backup(PlanetData store) {
        store.position = this.position.clone();
        store.dimensions = this.dimensions.clone();
        store.rotation = this.rotation.clone();
        store.size = this.size;
    }

    public void restore(PlanetData load) {
        ShaderBlock<PlanetData> block = VeilRenderSystem.getBlock(HubbleShaderBufferRegistry.PLANET_DATA.get());
        if (block == null) throw new IllegalStateException("Planet data has not been initialized!");
        this.position = load.position.clone();
        this.dimensions = load.dimensions.clone();
        this.rotation = load.rotation.clone();
        this.size = load.size;
        block.set(this);
        VeilRenderSystem.bind(HubbleShaderBufferRegistry.PLANET_DATA.get());
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

    public Matrix4f[] getInvRot() {
        Matrix4f[] invRot = new Matrix4f[SIZE];
        for (int i = 0; i < rotation.length; i++) {
            if (rotation[i] == null) continue;
            invRot[i] = rotation[i].invert();
        }
        return invRot;
    }

    public SimpleArrayTexture createArrayTexture() {
        return new SimpleArrayTexture(textures);
    }

    public int getSize() {
        return size;
    }

    public void addValues(Vector3f pos, Vector3f dims, Matrix4f rot, ResourceLocation texture) {
        if (size >= SIZE) throw new RuntimeException("Size of planet data cannot be greater than max size ("+SIZE+")");
        this.position[size] = pos;
        this.dimensions[size] = dims;
        this.rotation[size] = rot;
        textures[size] = texture;
        size++;
    }
}
