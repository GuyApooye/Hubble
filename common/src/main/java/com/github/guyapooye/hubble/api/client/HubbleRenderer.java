package com.github.guyapooye.hubble.impl.client;

import com.github.guyapooye.hubble.impl.client.shader.block.SunData;
import com.github.guyapooye.hubble.impl.client.shader.block.PlanetData;
import com.github.guyapooye.hubble.api.client.util.ImplicitRenderStateHolder;
import com.github.guyapooye.hubble.registry.HubbleShaderBufferRegistry;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostProcessingManager;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.system.NativeResource;

import java.util.*;

public final class HubbleRenderer implements NativeResource {

    private final static HubbleRenderer instance = new HubbleRenderer();
    private final Map<ResourceLocation, ImplicitRenderStateHolder> objectsToRender = new HashMap<>();
    private final PlanetData planetData;
    private final SunData lightData;

    private HubbleRenderer() {
        planetData = new PlanetData();
        lightData = new SunData();
    }

    @ApiStatus.Internal
    public static void bootstrap() {
    }

    public static HubbleRenderer getInstance() {
        return instance;
    }

    public void preRender() {

        HubbleClientManager.getInstance().allObjects().forEach((id, object) -> {
            ImplicitRenderStateHolder renderData = objectsToRender.get(id);
            renderData.update(object);
        });

        for (ImplicitRenderStateHolder data : objectsToRender.values()) {
            data.value().setup();
        }

        this.planetData.update();
        this.lightData.update();

    }

    public void render(MatrixStack matrixStack, MultiBufferSource.BufferSource buffer, Camera camera) {
        for (ImplicitRenderStateHolder data : objectsToRender.values()) {
            data.value().render(matrixStack, buffer, camera);
        }
    }

    public void postRender() {
        objectsToRender.clear();
        planetData.clear();
        lightData.clearNoUpdate();


        PostProcessingManager postManager = VeilRenderSystem.renderer().getPostProcessingManager();
//        postManager.runPipeline(postManager.getPipeline(PLANET));
//        postManager.runPipeline(postManager.getPipeline(BLOOM));
    }

    @Override
    public void free() {
        VeilRenderSystem.unbind(HubbleShaderBufferRegistry.PLANET_DATA.get());
        VeilRenderSystem.unbind(HubbleShaderBufferRegistry.LIGHT_DATA.get());
    }

    public PlanetData getPlanetData() {
        return planetData;
    }

    public SunData getLightData() {
        return lightData;
    }
}
