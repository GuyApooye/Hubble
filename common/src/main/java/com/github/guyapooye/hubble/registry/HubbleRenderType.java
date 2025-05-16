package com.github.guyapooye.hubble.registry;

import com.mojang.blaze3d.vertex.VertexFormat;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import static com.github.guyapooye.hubble.HubbleClient.PLANET;
import static com.github.guyapooye.hubble.HubbleClient.SUN;

public final class HubbleRenderType extends RenderType {

    public HubbleRenderType(String string, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
        super(string, vertexFormat, mode, i, bl, bl2, runnable, runnable2);
    }

    public static RenderType planet(ResourceLocation texture) {
        return VeilRenderType.get(PLANET, texture);
    }

    public static RenderType sun() {
        return VeilRenderType.get(SUN);
    }


}
