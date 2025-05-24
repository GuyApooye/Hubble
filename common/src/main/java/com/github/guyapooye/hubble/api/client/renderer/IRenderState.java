package com.github.guyapooye.hubble.api.client.renderer;

import com.github.guyapooye.hubble.api.object.HubbleObject;
import foundry.veil.api.client.render.MatrixStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;

public interface IRenderState<T extends HubbleObject<?>> {

    default void setup() {}

    default void render(MatrixStack matrixStack, Camera camera) {}

    default void clear() {}
    
    default void update(T load, float partialTicks) {}

    default void load(T load) {}

    @SuppressWarnings("unchecked")
    default boolean tryUpdate(HubbleObject<?> object, float partialTicks) {
        if (!getClass().isInstance(object)) return false;
        update((T) object, partialTicks);
        return true;
    }

    @SuppressWarnings("unchecked")
    default boolean tryLoad(HubbleObject<?> object) {
        if (!getClass().isInstance(object)) return false;
        load((T) object);
        return true;
    }
}
