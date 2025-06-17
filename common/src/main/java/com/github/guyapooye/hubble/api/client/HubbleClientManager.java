package com.github.guyapooye.hubble.api.client;

import com.github.guyapooye.hubble.api.body.CelestialBody;
import com.github.guyapooye.hubble.client.editor.HubbleObjectInspector;
import com.github.guyapooye.hubble.client.editor.HubbleReentryInspector;
import foundry.veil.Veil;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class HubbleClientManager {
    private static final HubbleClientManager INSTANCE = new HubbleClientManager();
    private static final HubbleObjectInspector objectInspector = new HubbleObjectInspector(Veil.platform().isDevelopmentEnvironment());
    private static final HubbleReentryInspector reentryInspector = new HubbleReentryInspector();

    private final ConcurrentHashMap<ResourceLocation, CelestialBody<?>> allObjects = new ConcurrentHashMap<>();

    @ApiStatus.Internal
    public static void bootstrap() {
        VeilEventPlatform.INSTANCE.onVeilRendererAvailable(renderer -> {
            renderer.getEditorManager().add(objectInspector);
            renderer.getEditorManager().add(reentryInspector);
        });
    }

    private HubbleClientManager() {

    }

    public static HubbleClientManager getInstance() {
        return INSTANCE;
    }

    public Map<ResourceLocation, CelestialBody<?>> allObjects() {
        return allObjects;
    }

    public static HubbleObjectInspector getObjectInspector() {
        return objectInspector;
    }

    public static HubbleReentryInspector getReentryInspector() {
        return reentryInspector;
    }
}
