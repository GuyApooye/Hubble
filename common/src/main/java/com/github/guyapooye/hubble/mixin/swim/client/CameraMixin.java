package com.github.guyapooye.hubble.mixin.swim.client;

import com.github.guyapooye.hubble.HubbleUtil;
import com.github.guyapooye.hubble.ext.CameraExtension;
import com.github.guyapooye.hubble.ext.EntityExtension;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin implements CameraExtension {

    @Unique
    private Quaternionf hubble$rotation = new Quaternionf();

    @Shadow private Entity entity;

    @Inject(method = "setup", at = @At("HEAD"))
    private void setupSpaceRotation(BlockGetter blockGetter, Entity entity, boolean bl, boolean bl2, float f, CallbackInfo ci) {
        if (((EntityExtension)entity).hubble$canSpaceWalk() && HubbleUtil.shouldSwimInSpace(entity.level().dimension())) {
            if (Minecraft.getInstance().options.getCameraType() == CameraType.THIRD_PERSON_FRONT) {
                hubble$rotation = ((EntityExtension)entity).hubble$getViewRotation(f).invert(new Quaternionf());
                return;
            }
            hubble$rotation = new Quaternionf().set(((EntityExtension)entity).hubble$getViewRotation(f));
        }
    }

    @Inject(method = "rotation", at = @At("HEAD"), cancellable = true)
    private void returnSpaceRotation(CallbackInfoReturnable<Quaternionf> cir) {
        if (HubbleUtil.shouldSwimInSpace(entity.level().dimension()) && ((EntityExtension)entity).hubble$canSpaceWalk()) cir.setReturnValue(hubble$rotation);
    }

    @ModifyArg(method = "setRotation", at = @At(value = "INVOKE", target = "Lorg/joml/Vector3f;rotate(Lorg/joml/Quaternionfc;Lorg/joml/Vector3f;)Lorg/joml/Vector3f;", remap = false), index = 0)
    private Quaternionfc replaceWithSpaceRotation0(Quaternionfc quat) {
        if (HubbleUtil.shouldSwimInSpace(entity.level().dimension()) && ((EntityExtension)entity).hubble$canSpaceWalk()) return hubble$rotation;
        return quat;
    }

    @ModifyArg(method = "move", at = @At(value = "INVOKE", target = "Lorg/joml/Vector3f;rotate(Lorg/joml/Quaternionfc;)Lorg/joml/Vector3f;", remap = false), index = 0)
    private Quaternionfc replaceWithSpaceRotation1(Quaternionfc quat) {
        if (HubbleUtil.shouldSwimInSpace(entity.level().dimension()) && ((EntityExtension)entity).hubble$canSpaceWalk()) return hubble$rotation;
        return quat;
    }

    @Override
    public Quaternionf hubble$getRotation() {
        return hubble$rotation;
    }
}
