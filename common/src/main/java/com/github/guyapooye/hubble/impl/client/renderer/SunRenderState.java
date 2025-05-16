package com.github.guyapooye.hubble.client.renderer;

import com.github.guyapooye.hubble.client.HubbleRenderer;
import com.github.guyapooye.hubble.space.SunObject;
import foundry.veil.api.client.render.MatrixStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import org.joml.*;

import static com.github.guyapooye.hubble.client.util.BoxRenderer.renderBoxQuads;
import static com.github.guyapooye.hubble.client.util.BoxRenderer.renderBoxQuadsIO;

public class SunRenderState implements IRenderState<SunObject> {

    protected Vector3f position;
    protected Vector3f dimensions;
    protected Quaterniond rotation;
    protected Vector4f color;
    protected float intensity;

    public SunRenderState(SunObject sun) {
        this.position = sun.getPosition();
        this.dimensions = sun.getDimensions();
        this.rotation = sun.getRotation();
        this.color = sun.getColor();
        this.intensity = sun.getIntensity();
    }

    @Override
    public void setup() {
        IRenderState.super.setup();
        HubbleRenderer.getInstance().getLightData().addValuesNoUpdate(position, dimensions, rotation.get(new Matrix4f()), new Vector3f(this.color.x(), this.color.y(), this.color.z()), intensity);
    }

    @Override
    public void render(MatrixStack matrixStack, MultiBufferSource.BufferSource buffer, Camera camera) {
        IRenderState.super.render(matrixStack, buffer, camera);
    }

    @Override
    public void update(SunObject load, float partialTicks) {
        IRenderState.super.update(load, partialTicks);
        position.lerp(load.getPosition(), partialTicks);
        dimensions.lerp(load.getDimensions(), partialTicks);
        rotation.slerp(load.getRotation(), partialTicks);
        color.lerp(load.getColor(), partialTicks);
        intensity = Mth.lerp(intensity, load.getIntensity(), partialTicks);
    }

    @Override
    public void load(SunObject load) {
        IRenderState.super.load(load);
        this.position = load.getPosition();
        this.dimensions = load.getDimensions();
        this.rotation = load.getRotation();
        this.color = load.getColor();
        this.intensity = load.getIntensity();
    }
}
