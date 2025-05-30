package com.github.guyapooye.hubble.mixin.veil.client;

import com.github.guyapooye.hubble.ext.VeilShaderBufferLayoutBuilderExtension;
import foundry.veil.api.client.render.VeilShaderBufferLayout;
import foundry.veil.api.client.render.VeilShaderBufferLayout.Builder;
import io.github.ocelot.glslprocessor.api.grammar.GlslStructField;
import io.github.ocelot.glslprocessor.api.grammar.GlslTypeSpecifier;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import org.joml.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unchecked")
@Mixin(value = Builder.class, remap = false)
public abstract class VeilShaderBufferLayoutBuilderMixin<T> implements VeilShaderBufferLayoutBuilderExtension<T> {
    @Unique
    private static final Vector4f hubble$EMPTY_VEC4 = new Vector4f();
    @Unique
    private static final Vector3f hubble$EMPTY_VEC3 = new Vector3f();
    @Unique
    private static final Matrix4f hubble$EMPTY_MAT4 = new Matrix4f();

    @Shadow @Final private List<GlslStructField> structFields;

    @Shadow @Final private Map<String, VeilShaderBufferLayout.FieldSerializer<T>> fields;

    @Override
    public Builder<T> hubble$vec3s(String name, int size, Function<T, Vector3fc[]> serializer) {
        structFields.add(new GlslStructField(GlslTypeSpecifier.array(GlslTypeSpecifier.BuiltinType.VEC3, GlslNode.intConstant(size)), name));
        fields.put(name, (value, index, buffer) -> {
            Vector3fc[] array = serializer.apply(value);
            for (int i = 0; i < array.length; i++) {
                if (array[i] == null) {
                    hubble$EMPTY_VEC4.get(index + 16*i, buffer);
                    continue;
                }
                array[i].get(index + 16*i, buffer);
                hubble$EMPTY_VEC4.get(index + 16*i + 12, buffer);
            }
        });
        return (Builder<T>) (Object) this;
    }

    @Override
    public Builder<T> hubble$mat4s(String name, int size, Function<T, Matrix4fc[]> serializer) {
        structFields.add(new GlslStructField(GlslTypeSpecifier.array(GlslTypeSpecifier.BuiltinType.MAT4, GlslNode.intConstant(size)), name));
        fields.put(name, (value, index, buffer) -> {
            Matrix4fc[] array = serializer.apply(value);
            for (int i = 0; i < array.length; i++) {
                if (array[i] == null) {
                    hubble$EMPTY_MAT4.get(index + 64*i, buffer);
                    continue;
                }
                array[i].get(index + 64*i, buffer);
            }
        });
        return (Builder<T>) (Object) this;
    }

    @Override
    public Builder<T> hubble$f32s(String name, int size, Function<T, Float[]> serializer) {
        structFields.add(new GlslStructField(GlslTypeSpecifier.array(GlslTypeSpecifier.BuiltinType.FLOAT, GlslNode.intConstant(size)), name));
        fields.put(name, (value, index, buffer) -> {
            Float[] array = serializer.apply(value);
            for (int i = 0; i < array.length; i++) {
//                if (array[i] == null) {
//                    hubble$EMPTY_VEC4.get(index + 16*i, buffer);
//                    continue;
//                }
//                buffer.putFloat(index + 16*i, array[i]);
//                hubble$EMPTY_VEC3.get(index + 16*i + 1, buffer);

                if (array[i] == null) {
                    buffer.putFloat(index + 4*i, 0.0f);
                    continue;
                }
                buffer.putFloat(index + 4*i, array[i]);
            }
        });
        return (Builder<T>) (Object) this;
    }
}
