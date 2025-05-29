package com.github.guyapooye.hubble.impl.client.renderer;

import com.github.guyapooye.hubble.api.client.renderer.IRenderState;
import com.github.guyapooye.hubble.api.client.HubbleRenderer;
import com.github.guyapooye.hubble.client.util.BoxRenderer;
import com.github.guyapooye.hubble.impl.body.SunBody;
import com.mojang.blaze3d.vertex.*;
import foundry.veil.api.client.render.MatrixStack;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import org.joml.*;

public class SunRenderState implements IRenderState<SunBody> {

    protected Vector3f position;
    protected Vector3f dimensions;
    protected Quaterniond rotation;
    protected Vector3f color;
    protected float intensity;

    public SunRenderState(SunBody sun) {
        this.position = sun.getPosition();
        this.dimensions = sun.getDimensions();
        this.rotation = sun.getRotation();
        this.color = sun.getColor();
        this.intensity = sun.getIntensity();
    }

    @Override
    public void setup() {
        IRenderState.super.setup();
        HubbleRenderer.getInstance().getLightData().addValuesNoUpdate(position, dimensions.div(2, new Vector3f()), rotation.get(new Matrix4f()), color, intensity);
    }

    @Override
    public void render(MatrixStack matrixStack, Camera camera) {
        IRenderState.super.render(matrixStack, camera);
//        VertexArray vertexArray = VertexArray.create();
//        vertexArray.upload(buildSun(position, dimensions.div(2, new Vector3f()), new Vector3f(color), rotation, matrixStack, camera), VertexArray.DrawUsage.DYNAMIC);
//        vertexArray.drawWithRenderType(HubbleRenderType.sun());
    }

    public static MeshData buildSun(Vector3fc pos, Vector3fc dims, Vector3fc color, Quaterniondc rot, MatrixStack matrixStack, Camera camera) {
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        matrixStack.matrixPush();
        Vector3f center = pos.sub(camera.getPosition().toVector3f(), new Vector3f());
        matrixStack.rotateAround(rot, center.x, center.y, center.z);
        buildLayer(center, dims, color, 0, matrixStack, builder, camera);
        buildLayer(center, dims, color, 1, matrixStack, builder, camera);
        buildLayer(center, dims, color, 2, matrixStack, builder, camera);
        buildLayer(center, dims, color, 3, matrixStack, builder, camera);
        matrixStack.matrixPop();

        return builder.buildOrThrow();
    }

    public static void buildLayer(Vector3fc pos, Vector3fc dims, Vector3fc color, int i, MatrixStack matrixStack, BufferBuilder builder, Camera camera) {

        PoseStack.Pose pose = matrixStack.pose();
        Vector3f dim = dims.mul(1-0.2f*i, new Vector3f());
        BoxRenderer.renderBoxQuadsIO(pose, (Vector3f) pos, dim, color.mul(0.25f*(1+i), new Vector3f()), builder);

    }

    @Override
    public void update(SunBody load, float partialTicks) {
        IRenderState.super.update(load, partialTicks);
        position.lerp(load.getPosition(), partialTicks);
        dimensions.lerp(load.getDimensions(), partialTicks);
        rotation.slerp(load.getRotation(), partialTicks);
        color.lerp(load.getColor(), partialTicks);
        intensity = Mth.lerp(intensity, load.getIntensity(), partialTicks);
    }

    @Override
    public void load(SunBody load) {
        IRenderState.super.load(load);
        this.position = load.getPosition();
        this.dimensions = load.getDimensions();
        this.rotation = load.getRotation();
        this.color = load.getColor();
        this.intensity = load.getIntensity();
    }
}
