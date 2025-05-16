package com.github.guyapooye.hubble;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;


public final class Hubble {
    public static final String MOD_ID = "hubble";
    public final static ResourceLocation SPACE = Hubble.path("space");
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
    }



    public static ResourceLocation path(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
