package com.github.guyapooye.hubble.neoforge.event;

import com.github.guyapooye.hubble.event.HubbleEventHooks;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

@EventBusSubscriber
public class HubbleNeoForgeEventHooks {

    public static void register(IEventBus eventBus) {
        HubbleNeoForgeEventHooks instance = new HubbleNeoForgeEventHooks();
        eventBus.register(instance);
    }

    private HubbleNeoForgeEventHooks() {}

    @SubscribeEvent
    public void onServerLevelStartup(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) HubbleEventHooks.onLevelStartup(level);
    }

}
