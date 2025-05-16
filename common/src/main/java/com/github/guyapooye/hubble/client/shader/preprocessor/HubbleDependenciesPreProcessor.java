package com.github.guyapooye.hubble.impl.client.shader.preprocessor;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.processor.ShaderBufferProcessor;
import foundry.veil.api.client.render.shader.processor.ShaderPreProcessor;
import io.github.ocelot.glslprocessor.api.GlslSyntaxException;
import io.github.ocelot.glslprocessor.api.node.GlslNodeList;
import io.github.ocelot.glslprocessor.api.node.GlslTree;
import io.github.ocelot.glslprocessor.lib.anarres.cpp.LexerException;

import java.io.IOException;
import java.util.List;

public class HubbleDependenciesPreProcessor implements ShaderPreProcessor {
    @Override
    public void modify(Context ctx, GlslTree tree) throws IOException, GlslSyntaxException, LexerException {
        if (ctx.name().getNamespace().equals("hubble")) {
            List<String> directives = tree.getDirectives();
            directives.add("#veil:buffer veil:camera VeilCamera");
            directives.add("#veil:buffer hubble:planet_data PlanetData");
            directives.add("#veil:buffer hubble:sun_data SunData");
            directives.add("#include veil:color_utilities");
            directives.add("#include veil:space_helper");
        }

    }
}
