package com.github.guyapooye.hubble.api.client.util;

import com.github.guyapooye.hubble.api.client.render.IRenderState;
import com.github.guyapooye.hubble.api.body.CelestialBody;
import net.minecraft.client.Minecraft;

public class ImplicitRenderStateHolder {
    private static final Minecraft mc = Minecraft.getInstance();

    private IRenderState<?> value;

    public IRenderState<?> value() {
        return value;
    }

    public ImplicitRenderStateHolder(IRenderState<?> value) {
        this.value = value;
    }

    public void update(CelestialBody<?> state) {
        if (!value.tryUpdate(state, mc.getTimer().getGameTimeDeltaTicks())) value = state.createRenderState();
    }

    public void load(CelestialBody<?> state) {
        if (!value.tryLoad(state)) value = state.createRenderState();
    }
}
