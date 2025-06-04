package com.github.guyapooye.hubble.mixin.world.client;

import com.github.guyapooye.hubble.HubbleUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
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

    @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/FogRenderer;setupColor(Lnet/minecraft/client/Camera;FLnet/minecraft/client/multiplayer/ClientLevel;IF)V"))
    private void clearFog(Camera camera, float f, ClientLevel clientLevel, int i, float g, Operation<Void> original) {
        if (!HubbleUtil.shouldExecuteSpace(clientLevel.dimension())) {
            original.call(camera, f, clientLevel, i, g);
            return;
        }
        RenderSystem.clearColor(0.0f,0.0f,0.0f,0.0f);
    }

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

//    @Inject(method = "drawStars", at = @At("HEAD"), cancellable = true)
//    private void drawHubbleStars(Tesselator tesselator, CallbackInfoReturnable<MeshData> cir) {
//        RandomSource randomSource = RandomSource.create(10842L);
//        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
//
//        for(int i = 0; i < 1500; ++i) {
//            float x = randomSource.nextFloat() * 2.0F - 1.0F;
//            float y = randomSource.nextFloat() * 2.0F - 1.0F;
//            float z = randomSource.nextFloat() * 2.0F - 1.0F;
//            float scale = 0.25F + randomSource.nextFloat() * 0.75F;
//            float length = Mth.lengthSquared(x, y, z);
//            if (!(length <= 0.010000001F) && !(length >= 1.0F)) {
//                float t = randomSource.nextFloat();
//                float intensity = randomSource.nextFloat();
//                intensity *= intensity;
//                intensity *= intensity;
//                Vector3f color = new Vector3f();
//                if (t > 0.5) {
//                    t = 2 * t - 1;
//                    t *= t * 0.8f;
//                    WHITE.lerp(ORANGE, t, color).mul(intensity);
//                } else {
//                    t = 2 * t;
//                    t *= t * 0.8f;
//                    WHITE.lerp(BLUE, t, color).mul(intensity);
//                }
//
//                Vector3f vector3f = (new Vector3f(x, y, z)).normalize(100.0F);
//                float angle = (float)(randomSource.nextDouble() * Mth.PI * 2.0);
//                Quaternionf quaternionf = (new Quaternionf()).rotateTo(new Vector3f(0.0F, 0.0F, -1.0F), vector3f).rotateZ(angle);
//                bufferBuilder.addVertex(vector3f.add((new Vector3f(scale, -scale, 0.0F)).rotate(quaternionf))).setColor(color.x, color.y, color.z, 1.0f);
//                bufferBuilder.addVertex(vector3f.add((new Vector3f(scale, scale, 0.0F)).rotate(quaternionf))).setColor(color.x, color.y, color.z, 1.0f);
//                bufferBuilder.addVertex(vector3f.add((new Vector3f(-scale, scale, 0.0F)).rotate(quaternionf))).setColor(color.x, color.y, color.z, 1.0f);
//                bufferBuilder.addVertex(vector3f.add((new Vector3f(-scale, -scale, 0.0F)).rotate(quaternionf))).setColor(color.x, color.y, color.z, 1.0f);
//            }
//        }
//
//        cir.setReturnValue(bufferBuilder.buildOrThrow());
//    }
}
