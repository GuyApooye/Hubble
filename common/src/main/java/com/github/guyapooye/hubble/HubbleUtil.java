package com.github.guyapooye.hubble;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import static com.github.guyapooye.hubble.Hubble.SPACE;

public class HubbleUtil {
    public static boolean shouldExecuteSpace(ResourceKey<Level> dimension) {
        return dimension.location().equals(SPACE);
    }


    public static boolean shouldSwimInSpace(ResourceKey<Level> dimension) {
        return dimension.location().equals(SPACE)/* && false*/;
    }

}
