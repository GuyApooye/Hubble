package com.github.guyapooye.hubble.mixin.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import foundry.veil.api.client.render.CameraMatrices;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CameraMatrices.class, remap = false)
public class DebugCameraMatricesMixin {
    @Shadow private float farPlane;

    @Shadow private float nearPlane;

    @Shadow @Final private Matrix4f inverseProjectionMatrix;

    @Shadow @Final private Matrix4f projectionMatrix;

    @Inject(method = "update", at = @At("TAIL"))
    private void printDebug(Matrix4fc projection, Matrix4fc modelView, double x, double y, double z, CallbackInfo ci) {
//        Vector4f a = new Vector4f();
//        nearPlane = RenderSystem.getProjectionMatrix().perspectiveNear() inverseProjectionMatrix.transform(0.0f,0.0f,-10.0f, 1.0f, a).z();
//        farPlane = inverseProjectionMatrix.transform(0.0f,0.0f,10.0f, 1.0f, a).z();
//        System.out.println();
//        System.out.println("far: "+ farPlane);
//        System.out.println("near: " + nearPlane);
//        System.out.println();
//        nearPlane = inverseProjectionMatrix.perspectiveNear();
//        farPlane = inverseProjectionMatrix.perspectiveFar();
    }

    @Inject(method = "updateRenderSystem", at = @At("TAIL"))
    private void printDebug(CallbackInfo ci) {
//        Vector4f a = new Vector4f();
//        nearPlane = inverseProjectionMatrix.transform(0.0f,0.0f,-10.0f, 1.0f, a).z();
//        farPlane = inverseProjectionMatrix.transform(0.0f,0.0f,10.0f, 1.0f, a).z();
//        System.out.println();
//        System.out.println("far: "+ farPlane);
//        System.out.println("near: " + nearPlane);
//        System.out.println();
//        nearPlane = inverseProjectionMatrix.perspectiveNear();
//        farPlane = inverseProjectionMatrix.perspectiveFar();
    }
}
