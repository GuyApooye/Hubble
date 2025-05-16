package com.github.guyapooye.hubble.space;

import com.github.guyapooye.hubble.client.renderer.IRenderState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class HubbleObject<T> {
    protected Vector3f position;
    protected ResourceLocation id;
    protected ResourceKey<Level> dimension;

    public HubbleObject(ResourceLocation id, ResourceKey<Level> dimension) {
        this.id = id;
        this.dimension = dimension;
    }

    public Vector3f getPosition() {
        return position;
    }

    public ResourceLocation getId() {
        return id;
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public void setId(ResourceLocation id) {
        this.id = id;
    }

    public void setDimension(ResourceKey<Level> dimension) {
        this.dimension = dimension;
    }

    @Environment(EnvType.CLIENT)
    public IRenderState<T> createRenderState() {
        return new IRenderState<>() {
        };
    }

}
