package com.github.guyapooye.hubble.client.util;

import com.github.guyapooye.hubble.client.renderer.IRenderState;
import com.github.guyapooye.hubble.space.HubbleObject;
import net.minecraft.client.Minecraft;

public class ImplicitRenderStateHolder {
    private static final Minecraft mc = Minecraft.getInstance();

    private IRenderState<?> value;

    public IRenderState<?> value() {
        return value;
    }

    public void update(HubbleObject<?> state) {
        if (!value.tryUpdate(state, mc.getTimer().getGameTimeDeltaTicks())) value = state.createRenderState();
    }

    public void load(HubbleObject<?> state) {
        if (!value.tryLoad(state)) value = state.createRenderState();
    }
}
