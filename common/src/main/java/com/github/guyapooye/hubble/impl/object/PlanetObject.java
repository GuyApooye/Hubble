package com.github.guyapooye.hubble.space;

import com.github.guyapooye.hubble.client.renderer.IRenderState;
import com.github.guyapooye.hubble.client.renderer.PlanetRenderState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaterniond;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class PlanetObject extends HubbleObject<PlanetObject> {

    protected Quaterniond rotation;
    protected Vector3f dimensions;
    protected ResourceLocation texture;

    public PlanetObject(ResourceLocation id, ResourceKey<Level> dimension) {
        super(id, dimension);
    }

    public Vector3f getDimensions() {
        return dimensions;
    }

    public Quaterniond getRotation() {
        return rotation;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public IRenderState<PlanetObject> createRenderState() {
        return new PlanetRenderState(this);
    }
}
