package com.github.guyapooye.hubble.api.client;

import com.github.guyapooye.hubble.HubbleUtil;
import com.github.guyapooye.hubble.client.render.HubbleReentryManager;
import com.github.guyapooye.hubble.client.render.shader.block.AtmosphereData;
import com.github.guyapooye.hubble.client.render.shader.block.SunData;
import com.github.guyapooye.hubble.client.render.shader.block.PlanetData;
import com.github.guyapooye.hubble.api.client.util.ImplicitRenderStateHolder;
import com.github.guyapooye.hubble.ext.EntityExtension;
import com.github.guyapooye.hubble.registry.HubbleShaderBufferRegistry;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.post.PostProcessingManager;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.system.NativeResource;

import java.util.*;

import static com.github.guyapooye.hubble.HubbleClient.*;

public final class HubbleRenderer implements NativeResource {

    private static final HubbleRenderer instance = new HubbleRenderer();
    private static final Minecraft minecraft = Minecraft.getInstance();
    private final HubbleReentryManager reentryManager = new HubbleReentryManager();
    private final Map<ResourceLocation, ImplicitRenderStateHolder> objectsToRender = new HashMap<>();
    private final PlanetData planetData;
    private final AtmosphereData atmosphereData;
    private final SunData sunData;

    private HubbleRenderer() {
        planetData = new PlanetData();
        atmosphereData = new AtmosphereData();
        sunData = new SunData();
    }

    @ApiStatus.Internal
    public static void bootstrap() {
        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage((stage, levelRenderer, bufferSource, matrixStack, matrix4fc, matrix4fc1, i, deltaTracker, camera, frustum) -> {
            if (stage == VeilRenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
                if (HubbleUtil.shouldExecuteSpace(minecraft.level.dimension())) {
                    HubbleRenderer renderer = HubbleRenderer.getInstance();

                    renderer.preRender();
                    renderer.render(matrixStack, camera);
                    renderer.postRender();

                }
            }
        });
    }

    public static HubbleRenderer getInstance() {
        return instance;
    }

    public void preRender() {

        EntityExtension player = ((EntityExtension) minecraft.player);

        while (ROLL.get().consumeClick()) {
            player.hubble$roll(-0.02f);
        }
        while (ROLL_INVERSE.get().consumeClick()) {
            player.hubble$roll(0.02f);
        }

        objectsToRender.clear();
        planetData.clear();
        atmosphereData.clear();
        sunData.clear();

        HubbleClientManager.getInstance().allObjects().forEach((id, object) -> {
            ImplicitRenderStateHolder renderData = objectsToRender.computeIfAbsent(id, (rl) -> new ImplicitRenderStateHolder(object.createRenderState()));
            renderData.update(object);
        });

        for (ImplicitRenderStateHolder data : objectsToRender.values()) {
            data.value().setup();
        }

        this.planetData.update();
        this.atmosphereData.update();
        this.sunData.update();

    }

    public void render(MatrixStack matrixStack, Camera camera) {

        PostProcessingManager postManager = VeilRenderSystem.renderer().getPostProcessingManager();
        boolean renderFab = Minecraft.getInstance().levelRenderer.getTranslucentTarget() != null;

        suns:
        if (!HubbleClientManager.getObjectInspector().disableSuns()) {
            if (sunData.getLength() <= 0) break suns;
            postManager.runPipeline(postManager.getPipeline(SUN));
        }

        planets:
        if (!HubbleClientManager.getObjectInspector().disablePlanets()) {

            if (planetData.getLength() <= 0) break planets;

            PostPipeline planet = postManager.getPipeline(PLANET);

//            SimpleArrayTexture arrayTexture = new SimpleArrayTexture(
//                    ResourceLocation.withDefaultNamespace("textures/block/bricks.png"),
//                    ResourceLocation.withDefaultNamespace("textures/block/sand.png")
//            );

//            glActiveTexture(GL_TEXTURE0);
//
//            try {
//                arrayTexture.load(minecraft.getResourceManager());
//            } catch (IOException e) {
//                LOGGER.error("Error during texture loading!", e);
//                break planets;
//            }

            if (planet != null) {
//                planet.getOrCreateUniform("Textures").setInt(0);
                postManager.runPipeline(planet);
            }

//            arrayTexture.close();
        }


        for (ImplicitRenderStateHolder data : objectsToRender.values()) {
            data.value().render(matrixStack, camera);
        }



    }

    public void postRender() {

    }

    @Override
    public void free() {
        VeilRenderSystem.unbind(HubbleShaderBufferRegistry.PLANET_DATA.get());
        VeilRenderSystem.unbind(HubbleShaderBufferRegistry.ATMOSPHERE_DATA.get());
        VeilRenderSystem.unbind(HubbleShaderBufferRegistry.LIGHT_DATA.get());
        objectsToRender.clear();
    }

    public PlanetData getPlanetData() {
        return planetData;
    }

    public AtmosphereData getAtmosphereData() {
        return atmosphereData;
    }

    public SunData getSunData() {
        return sunData;
    }

    public HubbleReentryManager getReentryManager() {
        return reentryManager;
    }
}
