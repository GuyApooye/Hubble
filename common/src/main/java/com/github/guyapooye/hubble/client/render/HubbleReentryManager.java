package com.github.guyapooye.hubble.client.render;

import com.github.guyapooye.hubble.api.client.HubbleClientManager;
import com.github.guyapooye.hubble.api.client.HubbleRenderer;
import com.github.guyapooye.hubble.client.editor.HubbleReentryInspector;
import foundry.veil.api.client.render.VeilLevelPerspectiveRenderer;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.client.render.texture.TextureFilter;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;
import org.lwjgl.system.NativeResource;

import static com.github.guyapooye.hubble.HubbleClient.REENTRY_PASS_2;
import static com.github.guyapooye.hubble.HubbleClient.REENTRY_PASS_3;

public class HubbleReentryManager implements NativeResource {

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;

//    private final AdvancedFbo shadowMap;

    public HubbleReentryManager() {
//        this.shadowMap = new AdvancedFbo.Builder(WIDTH, HEIGHT).setBorderColor(0x00000000).setWrap(TextureFilter.Wrap.CLAMP_TO_BORDER, TextureFilter.Wrap.CLAMP_TO_BORDER).addColorRenderBuffer().setName("hubble:shadowmap/reentry").setDebugLabel("Reentry Shadow Map").build(true);
    }

    public static void bootstrap() {
//        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage((stage, levelRenderer, bufferSource, matrixStack, matrix4fc, matrix4fc1, i, deltaTracker, camera, frustum) -> {
//            if (stage == VeilRenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) HubbleRenderer.getInstance().getReentryManager().tick();
//        });
    }

    private void attachUniforms(ShaderProgram pass, HubbleReentryInspector reentryInspector) {
        pass.getOrCreateUniform("Direction").setVector(reentryInspector.getDirection().normalize());
        pass.getOrCreateUniform("Length").setFloat(reentryInspector.getLength());
        pass.getOrCreateUniform("Velocity").setFloat(reentryInspector.getVelocity());
        pass.getOrCreateUniform("Strength").setFloat(reentryInspector.getStrength());
        pass.getOrCreateUniform("Increment").setFloat(reentryInspector.getIncrement());
        pass.getOrCreateUniform("TaperSize").setFloat(reentryInspector.getTaperSize());
        pass.getOrCreateUniform("LightColor").setVector(reentryInspector.getLightColor());
        pass.getOrCreateUniform("TrailColor").setVector(reentryInspector.getTrailColor());
        pass.getOrCreateUniform("TrailColorHot").setVector(reentryInspector.getTrailColorHot());
        pass.getOrCreateUniform("BowShockColor").setVector(reentryInspector.getBowShockColor());
        pass.getOrCreateUniform("BowShockOffset").setFloat(reentryInspector.getBowShockOffset());
        pass.getOrCreateUniform("BowShockColorLerpOffset").setFloat(reentryInspector.getBowShockColorLerpOffset());
    }

    public void tick() {
        HubbleReentryInspector reentryInspector = HubbleClientManager.getReentryInspector();
        if (reentryInspector == null) return;

        ShaderProgram secondPass = VeilRenderSystem.renderer().getShaderManager().getShader(REENTRY_PASS_2);
        ShaderProgram thirdPass = VeilRenderSystem.renderer().getShaderManager().getShader(REENTRY_PASS_3);
        if (secondPass != null) {
            attachUniforms(secondPass, reentryInspector);
        }
        if (thirdPass != null) {
            attachUniforms(thirdPass, reentryInspector);
        }

//        VeilLevelPerspectiveRenderer.
    }

    @Override
    public void free() {
//        shadowMap.close();
    }
}
