package com.github.guyapooye.hubble.api.client;

import com.github.guyapooye.hubble.client.shader.block.SunData;
import com.github.guyapooye.hubble.client.shader.block.PlanetData;
import com.github.guyapooye.hubble.api.client.util.ImplicitRenderStateHolder;
import com.github.guyapooye.hubble.impl.client.renderer.SunRenderState;
import com.github.guyapooye.hubble.registry.HubbleShaderBufferRegistry;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.client.render.vertex.VertexArray;
import net.minecraft.client.Camera;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.system.NativeResource;

import java.util.*;

import static com.github.guyapooye.hubble.HubbleClient.*;

public final class HubbleRenderer implements NativeResource {

    private final static HubbleRenderer instance = new HubbleRenderer();
    private final Map<ResourceLocation, ImplicitRenderStateHolder> objectsToRender = new HashMap<>();
    private final PlanetData planetData;
    private final SunData sunData;

    private HubbleRenderer() {
        planetData = new PlanetData();
        sunData = new SunData();
    }

    @ApiStatus.Internal
    public static void bootstrap() {
        SunRenderState.setVertexArray(VertexArray.create());
    }

    public static HubbleRenderer getInstance() {
        return instance;
    }

    public void preRender() {
        objectsToRender.clear();
        planetData.clearNoUpdate();
        sunData.clearNoUpdate();

        HubbleClientManager.getInstance().allObjects().forEach((id, object) -> {
            ImplicitRenderStateHolder renderData = objectsToRender.computeIfAbsent(id, (rl) -> new ImplicitRenderStateHolder(object.createRenderState()));
            renderData.update(object);
        });

        for (ImplicitRenderStateHolder data : objectsToRender.values()) {
            data.value().setup();
        }

        this.planetData.update();
        this.sunData.update();

    }

    public void render(MatrixStack matrixStack, Camera camera) {

        PostProcessingManager postManager = VeilRenderSystem.renderer().getPostProcessingManager();
        if (HubbleClientManager.getObjectInspector().useRaycastSuns()) postManager.runPipeline(postManager.getPipeline(SUN_CAST));
        else postManager.runPipeline(postManager.getPipeline(SUN_MARCH));

        for (ImplicitRenderStateHolder data : objectsToRender.values()) {
            data.value().render(matrixStack, camera);
        }



    }

    public void postRender() {


//        RenderSystem.depthMask(true);
//        VeilRenderSystem.renderer().getShaderManager().getShader(Hubble.path("raymarch_sun")).setSampler("Depth",AdvancedFbo.getMainFramebuffer().toRenderTarget().getDepthTextureId());
    }

    @Override
    public void free() {
        VeilRenderSystem.unbind(HubbleShaderBufferRegistry.PLANET_DATA.get());
        VeilRenderSystem.unbind(HubbleShaderBufferRegistry.LIGHT_DATA.get());
        objectsToRender.clear();
    }

    public PlanetData getPlanetData() {
        return planetData;
    }

    public SunData getSunData() {
        return sunData;
    }
}
