package com.github.guyapooye.hubble.impl.client.render;

import com.github.guyapooye.hubble.api.body.AtmosphereSettings;
import com.github.guyapooye.hubble.api.client.render.IRenderState;
import com.github.guyapooye.hubble.api.client.HubbleRenderer;
import com.github.guyapooye.hubble.client.render.shader.block.AtmosphereData;
import com.github.guyapooye.hubble.client.render.shader.block.PlanetData;
import com.github.guyapooye.hubble.impl.body.PlanetBody;
import foundry.veil.api.client.render.MatrixStack;
import net.minecraft.client.Camera;
import net.minecraft.resources.ResourceLocation;
import org.joml.*;

import java.lang.Math;

import static com.github.guyapooye.hubble.client.util.BoxRenderer.renderBoxQuads;

public class PlanetRenderState implements IRenderState<PlanetBody> {
    protected Vector3f position;
    protected Vector3f dimensions;
    protected Quaterniond rotation;
    protected AtmosphereSettings atmosphereSettings;
    protected ResourceLocation texture;

    public PlanetRenderState(PlanetBody planet) {
        position = planet.getPosition();
        dimensions = planet.getDimensions();
        rotation = planet.getRotation();
        atmosphereSettings = planet.getAtmosphereSettings();
        texture = planet.getTexture();
    }

    @Override
    public void setup() {
        IRenderState.super.setup();
        PlanetData planetData = HubbleRenderer.getInstance().getPlanetData();
        AtmosphereData atmosphereData = HubbleRenderer.getInstance().getAtmosphereData();
        planetData.addValues(position, this.dimensions.div(2.0f, new Vector3f()), this.rotation.get(new Matrix4f()), texture);
        float scatteringStrength = atmosphereSettings.getStrength();
        Vector3f actualCoefficients = atmosphereSettings.getScatteringCoefficients().get(new Vector3f());
        actualCoefficients.x = (float) (Math.pow(400 / actualCoefficients.x, 4.0) * scatteringStrength);
        actualCoefficients.y = (float) (Math.pow(400 / actualCoefficients.y, 4.0) * scatteringStrength);
        actualCoefficients.z = (float) (Math.pow(400 / actualCoefficients.z, 4.0) * scatteringStrength);
        atmosphereData.addValues(atmosphereSettings.getDensityFalloff(), atmosphereSettings.getSize(), atmosphereSettings.getStrength(), actualCoefficients);
    }

    @Override
    public void render(MatrixStack matrixStack, Camera camera) {
        IRenderState.super.render(matrixStack, camera);
    }

    public void update(PlanetBody load, float partialTicks) {
        IRenderState.super.update(load, partialTicks);
        position.lerp(load.getPosition(), partialTicks);
        dimensions.lerp(load.getDimensions(), partialTicks);
        rotation.slerp(load.getRotation(), partialTicks);
        atmosphereSettings.update(load.getAtmosphereSettings(), partialTicks);
        texture = load.getTexture();
    }

    @Override
    public void load(PlanetBody load) {
        IRenderState.super.load(load);
        position = load.getPosition();
        dimensions = load.getDimensions();
        rotation = load.getRotation();
        atmosphereSettings.load(load.getAtmosphereSettings());
        texture = load.getTexture();
    }
}
