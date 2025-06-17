package com.github.guyapooye.hubble.client.render;

import com.github.guyapooye.hubble.api.client.HubbleClientManager;
import com.github.guyapooye.hubble.api.client.HubbleRenderer;
import com.github.guyapooye.hubble.client.editor.HubbleReentryInspector;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import foundry.veil.platform.VeilEventPlatform;

import static com.github.guyapooye.hubble.HubbleClient.REENTRY_PASS_2;

public class HubbleReentryManager {

    public static void bootstrap() {
        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage((stage, levelRenderer, bufferSource, matrixStack, matrix4fc, matrix4fc1, i, deltaTracker, camera, frustum) -> {
            if (stage == VeilRenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) HubbleRenderer.getInstance().getReentryManager().tick();
        });
    }

    public void tick() {
        HubbleReentryInspector reentryInspector = HubbleClientManager.getReentryInspector();
        if (reentryInspector == null) return;

        ShaderProgram secondPass = VeilRenderSystem.renderer().getShaderManager().getShader(REENTRY_PASS_2);
        secondPass.getOrCreateUniform("Direction").setVector(reentryInspector.getDirection());
        secondPass.getOrCreateUniform("Length").setFloat(reentryInspector.getLength());
        secondPass.getOrCreateUniform("Velocity").setFloat(reentryInspector.getVelocity());
        secondPass.getOrCreateUniform("Strength").setFloat(reentryInspector.getStrength());
        secondPass.getOrCreateUniform("Increment").setFloat(reentryInspector.getIncrement());
        secondPass.getOrCreateUniform("TaperSize").setFloat(reentryInspector.getTaperSize());
        secondPass.getOrCreateUniform("Color0").setVector(reentryInspector.getColor0());
        secondPass.getOrCreateUniform("Color1").setVector(reentryInspector.getColor1());
        secondPass.getOrCreateUniform("Color2").setVector(reentryInspector.getColor2());
    }
}
