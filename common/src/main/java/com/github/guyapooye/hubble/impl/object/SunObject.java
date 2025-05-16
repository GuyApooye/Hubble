package com.github.guyapooye.hubble.impl.object;

import com.github.guyapooye.hubble.api.client.renderer.IRenderState;
import com.github.guyapooye.hubble.api.object.HubbleObject;
import com.github.guyapooye.hubble.impl.client.renderer.SunRenderState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.joml.*;

public class SunObject extends HubbleObject<SunObject> {

    protected Quaterniond rotation;
    protected Vector3f color;
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

    public Vector3f getColor() {
        return color;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setDimensions(Vector3f dimensions) {
        this.dimensions = dimensions;
    }

    public void setRotation(Quaterniond rotation) {
        this.rotation = rotation;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    @Override
    public IRenderState<SunObject> createRenderState() {
        return new SunRenderState(this);
    }
}
