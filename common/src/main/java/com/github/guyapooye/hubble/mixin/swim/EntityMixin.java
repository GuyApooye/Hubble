package com.github.guyapooye.hubble.mixin.swim;

import com.github.guyapooye.hubble.HubbleUtil;
import com.github.guyapooye.hubble.ext.EntityExtension;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityExtension {

    private Quaternionf hubble$rotation = new Quaternionf();
    private Quaternionf hubble$rotation0 = new Quaternionf();

    @Shadow private Level level;

    @ModifyArg(method = "updateSwimming", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setSwimming(Z)V"))
    private boolean swimInSpace(boolean bl) {
        return bl || (hubble$canSpaceWalk() && HubbleUtil.shouldSwimInSpace(level.dimension()));
    }

    @ModifyReturnValue(method = "isInWater", at = @At(value = "RETURN"))
    private boolean isInSpace(boolean original) {
        return original || (hubble$canSpaceWalk() && HubbleUtil.shouldSwimInSpace(level.dimension()));
    }

    @ModifyReturnValue(method = "isNoGravity", at = @At(value = "RETURN"))
    private boolean noGravityInSpace(boolean original) {
        return original || (hubble$canSpaceWalk() && HubbleUtil.shouldSwimInSpace(level.dimension()));
    }

    @WrapOperation(method = "turn", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private float dontClampTurn(float f, float g, float h, Operation<Float> original) {
        if (hubble$canSpaceWalk() && HubbleUtil.shouldSwimInSpace(level.dimension())) return f;
        return original.call(f,g,h);
    }

    @Inject(method = "turn", at = @At("HEAD"))
    private void turnQuaternion(double d, double e, CallbackInfo ci) {
        if (hubble$canSpaceWalk() && HubbleUtil.shouldSwimInSpace(level.dimension())) {
            hubble$rotation0.set(hubble$rotation);
            hubble$rotation.rotateX((float) e * -0.15f * Mth.PI/180.0f);
            hubble$rotation.rotateY((float) d * -0.15f * Mth.PI/180.0f);
        }
    }

    @WrapOperation(method = "absRotateTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private float dontClampAbsRotateTo(float f, float g, float h, Operation<Float> original) {
        if (hubble$canSpaceWalk() && HubbleUtil.shouldSwimInSpace(level.dimension())) return f;
        return original.call(f,g,h);
    }

    @Inject(method = "getViewVector", at = @At("RETURN"), cancellable = true)
    private void returnSpaceViewVector(float f, CallbackInfoReturnable<Vec3> cir) {
        if (hubble$canSpaceWalk() && HubbleUtil.shouldSwimInSpace(level.dimension())) cir.setReturnValue(new Vec3(hubble$getViewRotation(f).transform(new Vector3f(0.0f,0.0f,-1.0f))));
    }

//    @ModifyReturnValue(method = "getXRot", at = @At("RETURN"))
//    private float returnSpaceXRot(float original) {
//        if (hubble$canSpaceWalk() && HubbleUtil.shouldSwimInSpace(level.dimension())) return hubble$rotation.getEulerAnglesXYZ(new Vector3f()).x;
//        return original;
//    }
//
//    @ModifyReturnValue(method = "getYRot", at = @At("RETURN"))
//    private float returnSpaceYRot(float original) {
//        if (hubble$canSpaceWalk() && HubbleUtil.shouldSwimInSpace(level.dimension())) return hubble$rotation.getEulerAnglesXYZ(new Vector3f()).y;
//        return original;
//    }
//
//    @Inject(method = "setXRot", at = @At("HEAD"))
//    private void setSpaceXRot(float f, CallbackInfo ci) {
//        Vector3f euler = hubble$rotation.getEulerAnglesXYZ(new Vector3f());
//        hubble$rotation.rotationXYZ(f, euler.y, euler.z);
//    }
//
//    @Inject(method = "setXRot", at = @At("HEAD"))
//    private void setSpaceYRot(float f, CallbackInfo ci) {
//        Vector3f euler = hubble$rotation.getEulerAnglesXYZ(new Vector3f());
//        hubble$rotation.rotationXYZ(euler.x, f, euler.z);
//    }


    @Override
    public Quaternionf hubble$getRotation() {
        return hubble$rotation;
    }

    @Override
    public Quaternionf hubble$getViewRotation(float t) {
        return hubble$rotation0.nlerp(hubble$rotation, t);
    }

    @Override
    public void hubble$setRotation(Quaternionf rotation) {
        this.hubble$rotation = rotation;
    }

    @Override
    public void hubble$setRotation0(Quaternionf rotation0) {
        this.hubble$rotation0 = rotation0;
    }

    @Override
    public boolean hubble$canSpaceWalk() {
        return true;
    }
}
