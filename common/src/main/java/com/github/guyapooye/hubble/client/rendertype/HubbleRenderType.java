package com.github.guyapooye.hubble.client.rendertype;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public final class HubbleRenderType extends RenderType {
    public HubbleRenderType(String string, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, vertexFormat, mode, i, bl, bl2, runnable, runnable2);
    }

    private static final RenderType REENTRY_PASS_1;

    public static RenderType reentryFirstPass() {
        return REENTRY_PASS_1;
    }

    static {
        CompositeState reentryFirstPass = RenderType.CompositeState.builder()
                .setLightmapState(LIGHTMAP)
                .setShaderState(RENDERTYPE_SOLID_SHADER)
                .setTextureState(BLOCK_SHEET_MIPPED)
                .createCompositeState(true);


                /*RenderType.CompositeState.builder()
                .setCullState(NO_CULL)
                .setDepthTestState(LEQUAL_DEPTH_TEST)
                .setTransparencyState(ADDITIVE_TRANSPARENCY)
                .setWriteMaskState(COLOR_WRITE)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
                .createCompositeState(true);*/

//        REENTRY_PASS_1 = create("hubble:reentry/pass_1", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 4194304, true, false, reentryFirstPass);
        REENTRY_PASS_1 = create("solidd", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 4194304, true, false, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_SOLID_SHADER).setTextureState(BLOCK_SHEET_MIPPED).createCompositeState(true));

    }

}
