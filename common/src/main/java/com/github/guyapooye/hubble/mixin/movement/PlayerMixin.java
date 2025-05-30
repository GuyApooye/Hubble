package com.github.guyapooye.hubble.mixin.movement;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyConstant(method = "travel", constant = {/*@Constant(doubleValue = 0.06),*/@Constant(doubleValue = 0.085)})
    private double disableStupidForSpace0(double constant) {
        return 0.0;
    }
}
