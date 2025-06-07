package com.github.guyapooye.hubble.fabric.event;

import com.github.guyapooye.hubble.event.HubbleEventHooks;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.server.level.ServerLevel;

public class HubbleFabricEventHooks {

    public static void register() {
        HubbleFabricEventHooks instance = new HubbleFabricEventHooks();
        ServerWorldEvents.LOAD.register((server, level) -> instance.onServerLevelStartup(level));
    }

    private HubbleFabricEventHooks() {}

    public void onServerLevelStartup(ServerLevel level) {
        HubbleEventHooks.onLevelStartup(level);
    }

}
