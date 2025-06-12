package com.github.guyapooye.hubble.api.client.render;

import com.github.guyapooye.hubble.api.body.CelestialBody;
import foundry.veil.api.client.render.MatrixStack;
import net.minecraft.client.Camera;

public interface IRenderState<T extends CelestialBody<?>> {

    default void setup() {}

    default void render(MatrixStack matrixStack, Camera camera) {}

    default void clear() {}
    
    default void update(T load, float partialTicks) {}

    default void load(T load) {}

    @SuppressWarnings("unchecked")
    default boolean tryUpdate(CelestialBody<?> object, float partialTicks) {
        if (!getClass().isInstance(object)) return false;
        update((T) object, partialTicks);
        return true;
    }

    @SuppressWarnings("unchecked")
    default boolean tryLoad(CelestialBody<?> object) {
        if (!getClass().isInstance(object)) return false;
        load((T) object);
        return true;
    }
}
