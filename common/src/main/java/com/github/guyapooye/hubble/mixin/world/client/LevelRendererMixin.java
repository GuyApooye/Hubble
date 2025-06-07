package com.github.guyapooye.hubble.mixin.world.client;

import com.github.guyapooye.hubble.HubbleUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Unique
    private static final Vector3f ORANGE = new Vector3f(1.0f,0.54f,0.195f);
    @Unique
    private static final Vector3f BLUE = new Vector3f(0.24f,0.7f,1.0f);
    @Unique
    private static final Vector3f WHITE = new Vector3f(1.0f,1.0f,1.0f);


    @Shadow @Nullable private ClientLevel level;

    @Shadow @Nullable private VertexBuffer starBuffer;


//    @Nullable private VertexArray starsVertexArray;
//
//    @Unique @Nullable private ByteBuffer cachedStarsIndexBuffer;
//    @Unique @Nullable private ByteBuffer cachedStarsVertexBuffer;

//    @Unique
//    @Nullable private BufferBuilder starsChachedBufferBuilder;

    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/FogRenderer;setupColor(Lnet/minecraft/client/Camera;FLnet/minecraft/client/multiplayer/ClientLevel;IF)V"))
    private void clearFog(Camera camera, float f, ClientLevel clientLevel, int i, float g, Operation<Void> original) {
        if (!HubbleUtil.shouldExecuteSpace(clientLevel.dimension())) {
            original.call(camera, f, clientLevel, i, g);
            return;
        }
        RenderSystem.clearColor(0.0f,0.0f,0.0f,0.0f);
    }
//
//    @SuppressWarnings("ForRemoval")
//    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V"))
//    private void onlyRenderStars(LevelRenderer instance, Matrix4f matrix4f, Matrix4f matrix4f2, float f, Camera camera, boolean bl, Runnable runnable, Operation<Void> original) {
//
////        if (starsVertexArray == null) starsVertexArray = VertexArray.create();
//
//        if (!HubbleUtil.shouldExecuteSpace(level.dimension())) {
//            original.call(instance, matrix4f, matrix4f2, f, camera, bl, runnable);
//            return;
//        }
//        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
//        FogRenderer.setupNoFog();
//
//        if (starsChachedBufferBuilder != null) {
////            RenderSystem.assertOnRenderThread();
////            VertexArrayBuilder builder = starsVertexArray.editFormat();
//
////            int indexCount = 4*1500;
//
////            int vertexBuffer = starsVertexArray.getOrCreateBuffer(VERTEX_BUFFER);
////            starsVertexArray.uploadVertexBuffer(vertexBuffer, cachedStarsVertexBuffer, VertexArray.DrawUsage.STATIC.getGlType());
////            builder.applyFrom(VERTEX_BUFFER, vertexBuffer, 0, DefaultVertexFormat.POSITION);
//
////            if (cachedStarsIndexBuffer != null) {
////                starsVertexArray.uploadIndexBuffer(cachedStarsIndexBuffer);
////            } else {
////                RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS).bind(indexCount);
////            }
////            starsVertexArray.setIndexCount(indexCount, VertexArray.IndexType.BYTE);
////            starsVertexArray.setDrawMode(VertexFormat.Mode.QUADS);
//
////            starsVertexArray.bind();
////            ShaderInstance shader = GameRenderer.getPositionShader();
////            shader.setDefaultUniforms(VertexFormat.Mode.QUADS, matrix4f, matrix4f2, Minecraft.getInstance().getWindow());
////            shader.apply();
////            starsVertexArray.draw();
////            shader.clear();
////            VertexArray.unbind();
//            this.starBuffer.bind();
//            this.starBuffer.upload(starsChachedBufferBuilder.storeMesh());
//            this.starBuffer.drawWithShader(matrix4f, matrix4f2, GameRenderer.getPositionShader());
//            VertexBuffer.unbind();
//            return;
//        }
//
//        this.starBuffer.bind();
//        this.starBuffer.drawWithShader(matrix4f, matrix4f2, GameRenderer.getPositionShader());
//        VertexBuffer.unbind();
//    }
//
//    @Inject(method = "drawStars", at = @At("HEAD"), cancellable = true)
//    private void dontDrawStarsIfCached(Tesselator tesselator, CallbackInfoReturnable<MeshData> cir) {
//        if (starsChachedBufferBuilder != null) cir.setReturnValue(null);
//    }
//
//    @Inject(method = "drawStars", at = @At("RETURN"))
//    private void cacheStars(Tesselator tesselator, CallbackInfoReturnable<MeshData> cir, @Local BufferBuilder builder) {
////        ByteBuffer indexBuffer = original.indexBuffer();
////        if (indexBuffer != null) cachedStarsIndexBuffer = indexBuffer.duplicate();
////        cachedStarsVertexBuffer = original.vertexBuffer().duplicate();
//        starsChachedBufferBuilder = builder;
//    }

    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V"))
    private void onlyRenderStars(LevelRenderer instance, Matrix4f matrix4f, Matrix4f matrix4f2, float f, Camera camera, boolean bl, Runnable runnable, Operation<Void> original) {
        if (!HubbleUtil.shouldExecuteSpace(level.dimension())) {
            original.call(instance, matrix4f, matrix4f2, f, camera, bl, runnable);
            return;
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        FogRenderer.setupNoFog();
        this.starBuffer.bind();
        this.starBuffer.drawWithShader(matrix4f, matrix4f2, GameRenderer.getPositionShader());
        VertexBuffer.unbind();
    }

}
