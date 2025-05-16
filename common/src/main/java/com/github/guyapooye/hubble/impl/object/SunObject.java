package com.github.guyapooye.hubble.space;

import com.github.guyapooye.hubble.client.renderer.IRenderState;
import com.github.guyapooye.hubble.client.renderer.SunRenderState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.joml.*;

public class SunObject extends HubbleObject<SunObject> {

    protected Quaterniond rotation;
    protected Vector4f color;
    protected float intensity;
    protected Vector3f dimensions;

    public SunObject(ResourceLocation id, ResourceKey<Level> dimension) {
        super(id, dimension);
    }

    public Vector3f getDimensions() {
        return dimensions;
    }

    public Quaterniond getRotation() {
        return rotation;
    }

    public Vector4f getColor() {
        return color;
    }

    public float getIntensity() {
        return intensity;
    }

    @Override
    public IRenderState<SunObject> createRenderState() {
        return new SunRenderState(this);
    }
}
