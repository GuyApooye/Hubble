package com.github.guyapooye.hubble.api.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import foundry.veil.api.client.render.texture.SimpleArrayTexture;
import org.lwjgl.opengl.GL12C;

public class OpenArrayTexture extends SimpleArrayTexture {

    protected void init(int format, int mipmapLevel, int width) {
        RenderSystem.assertOnRenderThreadOrInit();
        this.bind();
        if (mipmapLevel >= 0) {
            GlStateManager._texParameter(35866, 33085, mipmapLevel);
            GlStateManager._texParameter(35866, 33082, 0);
            GlStateManager._texParameter(35866, 33083, mipmapLevel);
            GlStateManager._texParameter(35866, 34049, 0.0F);
        }

        for(int i = 0; i <= mipmapLevel; ++i) {
            GL12C.glTexImage3D(35866, i, format, width >> i, height >> i, depth, 0, 6408, 5121, 0L);
        }

    }
}
