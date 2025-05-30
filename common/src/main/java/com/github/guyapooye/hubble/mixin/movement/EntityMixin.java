package com.github.guyapooye.hubble.mixin.movement;

import com.github.guyapooye.hubble.HubbleUtil;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow private Level level;

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
    private float dontClampHeadRotation(float f, float g, float h, Operation<Float> original) {
        if ((Object) this instanceof Player && HubbleUtil.shouldSwimInSpace(level.dimension())) return f;
        return original.call(f,g,h);
    }
}
