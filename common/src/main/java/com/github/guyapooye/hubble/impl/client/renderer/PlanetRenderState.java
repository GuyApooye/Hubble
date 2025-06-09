package com.github.guyapooye.hubble.impl.client.renderer;

import com.github.guyapooye.hubble.api.client.renderer.IRenderState;
import com.github.guyapooye.hubble.api.client.HubbleRenderer;
import com.github.guyapooye.hubble.registry.HubbleRenderType;
import com.github.guyapooye.hubble.impl.body.PlanetBody;
import com.mojang.blaze3d.vertex.*;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.vertex.VertexArray;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.*;

import static com.github.guyapooye.hubble.client.util.BoxRenderer.renderBoxQuads;

public class PlanetRenderState implements IRenderState<PlanetBody> {
    protected Vector3f position;
    protected Vector3f dimensions;
    protected Quaterniond rotation;
    protected ResourceLocation texture;

    public PlanetRenderState(PlanetBody planet) {
        position = planet.getPosition();
        dimensions = planet.getDimensions();
        rotation = planet.getRotation();
        texture = planet.getTexture();
    }

    @Override
    public void setup() {
        IRenderState.super.setup();
        HubbleRenderer.getInstance().getPlanetData().addValuesNoUpdate(position, this.dimensions.div(2.0f, new Vector3f()), this.rotation.get(new Matrix4f()));
    }

    @Override
    public void render(MatrixStack matrixStack, Camera camera) {
        IRenderState.super.render(matrixStack, camera);
        VertexArray vertexArray = VertexArray.create();

        vertexArray.upload(buildPlanet(position, dimensions, rotation, matrixStack, camera), VertexArray.DrawUsage.DYNAMIC);
        RenderType planet = HubbleRenderType.planet(texture);
        vertexArray.drawWithRenderType(planet);
//        renderPlanet(this.position, this.dimensions, this.rotation, this.texture, matrixStack, buffer, camera);
    }

    public static void renderPlanet(Vector3fc pos, Vector3fc dims, Quaterniondc rot, ResourceLocation texture, MatrixStack matrixStack, MultiBufferSource.BufferSource buffer, Camera camera) {

        matrixStack.matrixPush();
        RenderType planet = HubbleRenderType.planet(texture);
        VertexConsumer builder = buffer.getBuffer(planet);

        PoseStack.Pose pose = matrixStack.pose();
        Vector3f dim = dims.div(2, new Vector3f());
        Vector3f center = pos.sub(camera.getPosition().toVector3f(), new Vector3f());
        matrixStack.rotateAround(rot, center.x, center.y, center.z);

        renderBoxQuads(pose, center, dim, builder);

        buffer.endBatch(planet);
        matrixStack.matrixPop();


    }

    public static void renderPlanet(Vector3fc pos, Vector3fc dims, Quaterniondc rot, MatrixStack matrixStack, BufferBuilder builder, Camera camera) {

        matrixStack.matrixPush();
        PoseStack.Pose pose = matrixStack.pose();
        Vector3f dim = dims.div(2, new Vector3f());
        Vector3f center = pos.sub(camera.getPosition().toVector3f(), new Vector3f());
        matrixStack.rotateAround(rot, center.x, center.y, center.z);
        renderBoxQuads(pose, center, dim, builder);

        matrixStack.matrixPop();
    }

    public static MeshData buildPlanet(Vector3fc pos, Vector3fc dims, Quaterniondc rot, MatrixStack matrixStack, Camera camera) {
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        renderPlanet(pos, dims, rot, matrixStack, builder, camera);
        return builder.buildOrThrow();
    }

    public void update(PlanetBody load, float partialTicks) {
        IRenderState.super.update(load, partialTicks);
        position.lerp(load.getPosition(), partialTicks);
        dimensions.lerp(load.getDimensions(), partialTicks);
        rotation.slerp(load.getRotation(), partialTicks);
        texture = load.getTexture();
    }

    @Override
    public void load(PlanetBody load) {
        IRenderState.super.load(load);
        position = load.getPosition();
        dimensions = load.getDimensions();
        rotation = load.getRotation();
        texture = load.getTexture();
    }
}
