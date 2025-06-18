package com.github.guyapooye.hubble.client.render.rendertype;

import com.github.guyapooye.hubble.Hubble;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public final class HubbleRenderType extends RenderType {
    public HubbleRenderType(String string, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, vertexFormat, mode, i, bl, bl2, runnable, runnable2);
    }

    private static final RenderType REENTRY_PASS_1;
    private static final RenderType REENTRY_PASS_2;
    private static final RenderType REENTRY_PASS_3;

    private static final RenderType REENTRY_FINAL;

    private static final TransparencyStateShard ALPHA_BLENDING_TRANSPARENCY = new TransparencyStateShard("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ZERO);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    public static RenderType reentryPass1() {
        return REENTRY_PASS_1;
    }

    public static RenderType reentryPass2() {
        return REENTRY_PASS_2;
    }

    public static RenderType reentryPass3() {
        return REENTRY_PASS_3;
    }

    public static RenderType reentryFinal() {
        return REENTRY_FINAL;
    }

    static {
        CompositeState reentryFirstPass = RenderType.CompositeState.builder()
                .setCullState(NO_CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
                .setShaderState(new ShaderStateShard(() -> VeilRenderSystem.renderer().getShaderManager().getShader(Hubble.path("rendertype/reentry/pass_1")).toShaderInstance()))
                .createCompositeState(true);

        REENTRY_PASS_1 = create("hubble:reentry/pass_1", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 786432, true, true, reentryFirstPass);

        CompositeState reentrySecondPass = RenderType.CompositeState.builder()
                .setCullState(NO_CULL)
                .setWriteMaskState(COLOR_WRITE)
                .setTransparencyState(ALPHA_BLENDING_TRANSPARENCY)
                .setShaderState(new ShaderStateShard(() -> VeilRenderSystem.renderer().getShaderManager().getShader(Hubble.path("rendertype/reentry/pass_2")).toShaderInstance()))
                .createCompositeState(true);

        REENTRY_PASS_2 = create("hubble:reentry/pass_2", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 786432, true, true, reentrySecondPass);

        CompositeState reentryThirdPass = RenderType.CompositeState.builder()
                .setCullState(NO_CULL)
                .setWriteMaskState(COLOR_WRITE)
                .setTransparencyState(ALPHA_BLENDING_TRANSPARENCY)
                .setShaderState(new ShaderStateShard(() -> VeilRenderSystem.renderer().getShaderManager().getShader(Hubble.path("rendertype/reentry/pass_3")).toShaderInstance()))
                .createCompositeState(true);

        REENTRY_PASS_3 = create("hubble:reentry/pass_3", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 786432, true, false, reentryThirdPass);

        REENTRY_FINAL = VeilRenderType.layered(REENTRY_PASS_1, REENTRY_PASS_2, REENTRY_PASS_3);

    }

}
