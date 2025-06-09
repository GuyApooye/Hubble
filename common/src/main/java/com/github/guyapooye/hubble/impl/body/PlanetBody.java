package com.github.guyapooye.hubble.impl.body;

import com.github.guyapooye.hubble.api.client.renderer.IRenderState;
import com.github.guyapooye.hubble.api.body.CelestialBody;
import com.github.guyapooye.hubble.api.effects.AtmosphereSettings;
import com.github.guyapooye.hubble.impl.client.renderer.PlanetRenderState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaterniond;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

public class PlanetBody extends CelestialBody<PlanetBody> {

    private Quaterniond rotation;
    private Vector3f dimensions;
    private ResourceLocation texture;
    private AtmosphereSettings atmosphere;

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

    public void setDimensions(Vector3f dimensions) {
        this.dimensions = dimensions;
    }

    public void setRotation(Quaterniond rotation) {
        this.rotation = rotation;
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    @Override
    public IRenderState<PlanetBody> createRenderState() {
        return new PlanetRenderState(this);
    }

    public AtmosphereSettings getAtmosphere() {
        return atmosphere;
    }

    public void setAtmosphere(AtmosphereSettings atmosphere) {
        this.atmosphere = atmosphere;
    }
}
