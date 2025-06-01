package com.github.guyapooye.hubble.mixin.swim;

import com.github.guyapooye.hubble.HubbleUtil;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow private Level level;

    @Shadow public float xRotO;

    @Shadow private float xRot;

    @ModifyArg(method = "updateSwimming", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setSwimming(Z)V"))
    private boolean swimInSpace(boolean bl) {
        if (((Object) this) instanceof Player p) {
            return p.isAffectedByFluids() && (bl || HubbleUtil.shouldSwimInSpace(level.dimension()));
        }
        return bl || HubbleUtil.shouldSwimInSpace(level.dimension());
    }

    @ModifyReturnValue(method = "isInWater", at = @At(value = "RETURN"))
    private boolean isInSpace(boolean original) {
        return original || HubbleUtil.shouldSwimInSpace(level.dimension());
    }

    @ModifyReturnValue(method = "isNoGravity", at = @At(value = "RETURN"))
    private boolean noGravityInSpace(boolean original) {
        return original || HubbleUtil.shouldSwimInSpace(level.dimension());
    }

    @WrapOperation(method = "turn", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private float dontClampTurn(float f, float g, float h, Operation<Float> original) {
        if ((Object) this instanceof Player && HubbleUtil.shouldSwimInSpace(level.dimension())) return f;
        return original.call(f,g,h);
    }

    @WrapOperation(method = "absRotateTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;clamp(FFF)F"))
    private float dontClampAbsRotateTo(float f, float g, float h, Operation<Float> original) {
        if ((Object) this instanceof Player && HubbleUtil.shouldSwimInSpace(level.dimension())) return f;
        return original.call(f,g,h);
    }

//    @ModifyVariable(method = "setXRot", at = @At("HEAD"), argsOnly = true)
//    private float moduloSetRotation(float value) {
//        return (value+180)%360-180;
//    }
//
//    @Inject(method = "getXRot", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
//    private void moduloGetRotation(CallbackInfoReturnable<Float> cir) {
//        return Mth.wrapDegrees(xRot);
//    }
}
