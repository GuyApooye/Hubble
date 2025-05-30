package com.github.guyapooye.hubble;

import com.github.guyapooye.hubble.api.client.HubbleClientManager;
import com.github.guyapooye.hubble.client.editor.HubbleObjectInspector;
import com.github.guyapooye.hubble.api.client.HubbleRenderer;
import com.github.guyapooye.hubble.client.shader.preprocessor.HubbleDependenciesPreProcessor;
import com.github.guyapooye.hubble.registry.HubbleShaderBufferRegistry;
import com.mojang.logging.LogUtils;
import foundry.veil.Veil;
import foundry.veil.VeilClient;
import foundry.veil.api.client.render.dynamicbuffer.DynamicBufferType;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import static com.github.guyapooye.hubble.Hubble.SPACE;

public final class HubbleClient {

    private static final Minecraft mc = Minecraft.getInstance();
    public static final ResourceLocation PLANET = Hubble.path("planet");
    public static final ResourceLocation SUN = Hubble.path("sun");
    public static final ResourceLocation SUN_FB = Hubble.path("sun_fb");
    public static final ResourceLocation RENDERTYPE_PLANET = Hubble.path("rendertype/rendertype_planet");
    public static final ResourceLocation CELESTIAL_OBJECT = Hubble.path("celestial_object");
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void initClient() {

        HubbleClientManager.bootstrap();

        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage((stage, levelRenderer, bufferSource, matrixStack, matrix4fc, matrix4fc1, i, deltaTracker, camera, frustum) -> {
            if (stage == VeilRenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
                if (HubbleUtil.shouldExecuteSpace(mc.level.dimension())) {
                    HubbleRenderer renderer = HubbleRenderer.getInstance();

                    renderer.preRender();
                    renderer.render(matrixStack, camera);
                    renderer.postRender();

                }
            }
        });

        VeilEventPlatform.INSTANCE.onVeilRendererAvailable(renderer -> {
            HubbleShaderBufferRegistry.bootstrap();
            HubbleRenderer.bootstrap();
        });

        VeilEventPlatform.INSTANCE.onVeilAddShaderProcessors((resourceProvider, registry) -> {
            registry.addPreprocessorFirst(new HubbleDependenciesPreProcessor(), false);
        });

        VeilEventPlatform.INSTANCE.onFreeNativeResources(() -> {
            HubbleRenderer.getInstance().close();
        });
    }
}
