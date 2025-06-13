package com.github.guyapooye.hubble.api.client;

import com.github.guyapooye.hubble.client.shader.block.SunData;
import com.github.guyapooye.hubble.client.shader.block.PlanetData;
import com.github.guyapooye.hubble.api.client.util.ImplicitRenderStateHolder;
import com.github.guyapooye.hubble.ext.EntityExtension;
import com.github.guyapooye.hubble.registry.HubbleShaderBufferRegistry;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.post.PostPipeline;
import foundry.veil.api.client.render.post.PostProcessingManager;
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
    private final Map<ResourceLocation, ImplicitRenderStateHolder> objectsToRender = new HashMap<>();
    private final PlanetData planetData;
    private final SunData sunData;

    private HubbleRenderer() {
        planetData = new PlanetData();
        sunData = new SunData();
    }

    @ApiStatus.Internal
    public static void bootstrap() {
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
        sunData.clear();

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
