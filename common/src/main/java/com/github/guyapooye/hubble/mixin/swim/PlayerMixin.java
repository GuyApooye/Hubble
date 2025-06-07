package com.github.guyapooye.hubble.mixin.swim;

import com.github.guyapooye.hubble.HubbleUtil;
import com.github.guyapooye.hubble.ext.EntityExtension;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Player.class, priority = 900)
public abstract class PlayerMixin extends LivingEntity implements EntityExtension {

    @Shadow public abstract boolean isAffectedByFluids();

    @Shadow @Final private Abilities abilities;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @WrapMethod(method = "travel")
    private void transformTravel(Vec3 vec3, Operation<Void> original) {
        if (!hubble$canSpaceWalk() || !HubbleUtil.shouldSwimInSpace(level().dimension())) {
            original.call(vec3);
            return;
        }
        
        Quaternionf rotation = hubble$getRotation();
        Vector3f axis = vec3.toVector3f();

        if (jumping) axis.add(0.0f, -1.0f,0.0f);
        if (isShiftKeyDown()) axis.add(0.0f, 1.0f,0.0f);

        rotation.transform(axis);
        this.setDeltaMovement(getDeltaMovement().add(new Vec3(axis.mul(-0.02f))));
        this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        this.move(MoverType.SELF, this.getDeltaMovement());
        this.calculateEntityAnimation(this instanceof FlyingAnimal);

    }

    @Override
    public boolean hubble$canSpaceWalk() {
        return abilities != null && !abilities.flying;
    }
}
