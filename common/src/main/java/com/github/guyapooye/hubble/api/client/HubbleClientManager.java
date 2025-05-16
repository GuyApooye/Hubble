package com.github.guyapooye.hubble.impl.client;

import com.github.guyapooye.hubble.api.object.HubbleObject;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class HubbleClientManager {
    private static final HubbleClientManager INSTANCE = new HubbleClientManager();

    private final ConcurrentHashMap<ResourceLocation, HubbleObject> allObjects = new ConcurrentHashMap<>();

    @ApiStatus.Internal
    public static void bootstrap() {

    }

    private HubbleClientManager() {

    }

    public static HubbleClientManager getInstance() {
        return INSTANCE;
    }

    public Map<ResourceLocation, HubbleObject> allObjects() {
        return allObjects;
    }
}
