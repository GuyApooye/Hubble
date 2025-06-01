package com.github.guyapooye.hubble.mixin.swim;

import com.github.guyapooye.hubble.HubbleUtil;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(value = Player.class, priority = 900)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow public abstract boolean isAffectedByFluids();

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyConstant(method = "travel", constant = {/*@Constant(doubleValue = 0.06),*/@Constant(doubleValue = 0.085)})
    private double disableStupidForSpace(double constant) {
        return 0.0;
    }

    @WrapMethod(method = "travel")
    private void transformTravel(Vec3 vec3, Operation<Void> original) {
        if (!isAffectedByFluids() || !HubbleUtil.shouldSwimInSpace(level().dimension())) {
            original.call(vec3);
            return;
        }

        float rotRadians = (Mth.wrapDegrees(getXRot()) % 360) * Mth.PI/180.0f;
        super.travel(new Vec3(2.0f*vec3.x, -2.0f*vec3.z*Mth.sin(rotRadians), 2.0f*vec3.z*Mth.cos(rotRadians)));
    }
}
