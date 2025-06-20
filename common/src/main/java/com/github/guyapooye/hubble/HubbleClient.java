package com.github.guyapooye.hubble;

import com.github.guyapooye.hubble.api.client.HubbleClientManager;
import com.github.guyapooye.hubble.api.client.HubbleRenderer;
import com.github.guyapooye.hubble.client.render.HubbleReentryManager;
import com.github.guyapooye.hubble.client.render.shader.preprocessor.HubbleDependenciesPreProcessor;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import foundry.veil.Veil;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.util.Lazy;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

public final class HubbleClient {

    private static final Minecraft minecraft = Minecraft.getInstance();
    public static final ResourceLocation PLANET = Hubble.path("planet");
    public static final ResourceLocation SUN = Hubble.path("sun");
    public static final ResourceLocation REENTRY_PASS_2 = Hubble.path("rendertype/reentry/pass_2");
    public static final ResourceLocation REENTRY_PASS_3 = Hubble.path("rendertype/reentry/pass_3");
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Lazy<KeyMapping> ROLL = Lazy.lazy(() -> new KeyMapping("key.hubble.roll", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, "category.hubble.main"));
    public static final Lazy<KeyMapping> ROLL_INVERSE = Lazy.lazy(() -> new KeyMapping("key.hubble.inverse_roll", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_X, "category.hubble.main"));

    public static void initClient() {

        HubbleClientManager.bootstrap();
        HubbleRenderer.bootstrap();
        HubbleReentryManager.bootstrap();

        VeilEventPlatform.INSTANCE.onVeilRendererAvailable(veilRenderer -> {
            VeilRenderSystem.renderer().getDynamicBufferManger().setEnabled(true);
            VeilRenderSystem.renderer().getDynamicBufferManger().setActiveBuffers(Hubble.path("planet"), 2);
        });

        VeilEventPlatform.INSTANCE.onVeilAddShaderProcessors((resourceProvider, registry) -> {
            registry.addPreprocessorFirst(new HubbleDependenciesPreProcessor(), false);
        });

        VeilEventPlatform.INSTANCE.onFreeNativeResources(() -> {
            HubbleRenderer.getInstance().close();
        });
    }

}
