package com.github.guyapooye.hubble.client.render.rendertype;

import com.github.guyapooye.hubble.Hubble;
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

    public static RenderType reentry() {
        return REENTRY_FINAL;
    }

    static {
        CompositeState reentryFirstPass = RenderType.CompositeState.builder()
                .setCullState(NO_CULL)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
                .setShaderState(new ShaderStateShard(() -> VeilRenderSystem.renderer().getShaderManager().getShader(Hubble.path("rendertype/reentry/pass_1")).toShaderInstance()))
                .createCompositeState(true);

        REENTRY_PASS_1 = create("hubble:reentry/pass_1", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 786432, true, false, reentryFirstPass);

        CompositeState reentrySecondPass = RenderType.CompositeState.builder()
                .setCullState(NO_CULL)
                .setWriteMaskState(COLOR_WRITE)
                .setTransparencyState(ADDITIVE_TRANSPARENCY)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
                .setShaderState(new ShaderStateShard(() -> VeilRenderSystem.renderer().getShaderManager().getShader(Hubble.path("rendertype/reentry/pass_2")).toShaderInstance()))
                .createCompositeState(true);

        REENTRY_PASS_2 = create("hubble:reentry/pass_2", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 786432, true, false, reentrySecondPass);

        CompositeState reentryThirdPass = RenderType.CompositeState.builder()
                .setCullState(NO_CULL)
                .setWriteMaskState(COLOR_WRITE)
                .setTransparencyState(ADDITIVE_TRANSPARENCY)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
                .setShaderState(new ShaderStateShard(() -> VeilRenderSystem.renderer().getShaderManager().getShader(Hubble.path("rendertype/reentry/pass_3")).toShaderInstance()))
                .createCompositeState(true);

        REENTRY_PASS_3 = create("hubble:reentry/pass_3", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 786432, true, false, reentryThirdPass);

        REENTRY_FINAL = VeilRenderType.layered(REENTRY_PASS_1, REENTRY_PASS_2/*, REENTRY_PASS_2*/);

    }

}
