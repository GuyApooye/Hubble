package com.github.guyapooye.hubble.impl.body;

import com.github.guyapooye.hubble.api.body.AtmosphereSettings;
import com.github.guyapooye.hubble.api.client.render.IRenderState;
import com.github.guyapooye.hubble.api.body.CelestialBody;
import com.github.guyapooye.hubble.impl.client.render.PlanetRenderState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaterniond;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class PlanetBody extends CelestialBody<PlanetBody> {

    protected Quaterniond rotation;
    protected Vector3f dimensions;
    protected AtmosphereSettings atmosphereSettings;
    protected ResourceLocation texture;

    public PlanetBody(ResourceLocation id, ResourceKey<Level> dimension) {
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

    public AtmosphereSettings getAtmosphereSettings() {
        return atmosphereSettings;
    }

    public void setDimensions(Vector3f dimensions) {
        this.dimensions = dimensions;
    }

    public void setRotation(Quaterniond rotation) {
        this.rotation = rotation;
    }

    public void setAtmosphereSettings(AtmosphereSettings atmosphereSettings) {
        this.atmosphereSettings = atmosphereSettings;
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    @Override
    public IRenderState<PlanetBody> createRenderState() {
        return new PlanetRenderState(this);
    }
}
