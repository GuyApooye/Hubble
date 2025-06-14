package com.github.guyapooye.hubble.client.shader.block;

import com.github.guyapooye.hubble.ext.VeilShaderBufferLayoutBuilderExtension;
import com.github.guyapooye.hubble.registry.HubbleShaderBufferRegistry;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.VeilShaderBufferLayout;
import foundry.veil.api.client.render.shader.block.ShaderBlock;
import foundry.veil.api.client.render.texture.SimpleArrayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Arrays;

@SuppressWarnings("unchecked")
public final class PlanetData {

    public static final int SIZE = 25;
    private Vector3f[] position = new Vector3f[SIZE];
    private Vector3f[] dimensions = new Vector3f[SIZE];
    private Matrix4f[] rotation = new Matrix4f[SIZE];
    private ResourceLocation[] textures = new ResourceLocation[SIZE];
    private int length = 0;

    public PlanetData() {}

    public static VeilShaderBufferLayout<PlanetData> createLayout() {
        return ((VeilShaderBufferLayoutBuilderExtension<PlanetData>)((VeilShaderBufferLayoutBuilderExtension<PlanetData>)((VeilShaderBufferLayoutBuilderExtension<PlanetData>)((VeilShaderBufferLayoutBuilderExtension<PlanetData>)
                VeilShaderBufferLayout.builder())
                .hubble$vec3s("Pos", SIZE, PlanetData::getPosition))
                .hubble$vec3s("Dims", SIZE, PlanetData::getDimensions))
                .hubble$mat4s("Rot", SIZE, PlanetData::getRotation))
                .hubble$f32s("Size", SIZE, PlanetData::getSize)
                .integer("Length", PlanetData::getLength)
                .build();
    }

    private void setValues(Vector3f[] pos, Vector3f[] dims, Matrix4f[] rot, ResourceLocation[] textures, int length) {
        ShaderBlock<PlanetData> block = VeilRenderSystem.getBlock(HubbleShaderBufferRegistry.PLANET_DATA.get());
        if (block == null) throw new IllegalStateException("Planet data has not been initialized!");
        if (length >= SIZE) throw new RuntimeException("Size of planet data cannot be greater than max size ("+SIZE+")");
        if (pos.length > SIZE || dims.length > SIZE || rot.length > SIZE || textures.length > SIZE) throw new IllegalArgumentException("Incorrect data array size!");
        this.position = pos.clone();
        this.dimensions = dims.clone();
        this.rotation = rot.clone();
        this.textures = textures.clone();
        this.length = length;
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
        store.length = this.length;
    }

    public void restore(PlanetData load) {
        ShaderBlock<PlanetData> block = VeilRenderSystem.getBlock(HubbleShaderBufferRegistry.PLANET_DATA.get());
        if (block == null) throw new IllegalStateException("Planet data has not been initialized!");
        this.position = load.position.clone();
        this.dimensions = load.dimensions.clone();
        this.rotation = load.rotation.clone();
        this.length = load.length;
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

    public Float[] getSize() {
        Float[] size = new Float[SIZE];
        for (int i = 0; i < this.length; i++) {
            size[i] = dimensions[i].length()/Mth.sqrt(3.0f);
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

    public SimpleArrayTexture createArrayTexture() {
        return new SimpleArrayTexture(Arrays.copyOf(textures, length));
    }

    public int getLength() {
        return length;
    }

    public void addValues(Vector3f pos, Vector3f dims, Matrix4f rot, ResourceLocation texture) {
        if (length >= SIZE) throw new RuntimeException("Size of planet data cannot be greater than max size ("+SIZE+")");
        this.position[length] = pos;
        this.dimensions[length] = dims;
        this.rotation[length] = rot;
        textures[length] = texture;
        length++;
    }
}
