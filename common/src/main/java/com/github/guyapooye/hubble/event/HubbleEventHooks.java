package com.github.guyapooye.hubble.event;

import com.github.guyapooye.hubble.HubbleUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
//import org.valkyrienskies.mod.common.VSGameUtilsKt;


public class HubbleEventHooks {
    public static void onLevelStartup(ServerLevel level) {
        ResourceKey<Level> dimension = level.dimension();
        if (HubbleUtil.shouldExecuteSpace(dimension));
//            VSGameUtilsKt.getShipObjectWorld(level).updateDimension(dimension.registry() + ":" + dimension.location(), new Vector3d());
    }
}
