package com.github.guyapooye.hubble.ext;

import foundry.veil.api.client.render.VeilShaderBufferLayout;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;

import java.util.function.Function;

public interface VeilShaderBufferLayoutBuilderExtension<T> {
    VeilShaderBufferLayout.Builder<T> hubble$vec3s(String name, int size, Function<T, Vector3fc[]> serializer);
    VeilShaderBufferLayout.Builder<T> hubble$mat4s(String name, int size, Function<T, Matrix4fc[]> serializer);
    VeilShaderBufferLayout.Builder<T> hubble$f32s(String name, int size, Function<T, Float[]> serializer);
}
