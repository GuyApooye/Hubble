package com.github.guyapooye.hubble.registry;

import com.github.guyapooye.hubble.client.render.shader.LightData;
import com.github.guyapooye.hubble.client.render.shader.PlanetData;
import foundry.veil.Veil;
import foundry.veil.api.client.registry.VeilShaderBufferRegistry;
import foundry.veil.api.client.render.VeilShaderBufferLayout;
import foundry.veil.platform.registry.RegistrationProvider;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public final class HubbleShaderBufferRegistry {
    public final static Supplier<VeilShaderBufferLayout<PlanetData>> PLANET_DATA = () -> (VeilShaderBufferLayout<PlanetData>) VeilShaderBufferRegistry.REGISTRY.get(Veil.veilPath("planet_data"));
    public final static Supplier<VeilShaderBufferLayout<LightData>> LIGHT_DATA = () -> (VeilShaderBufferLayout<LightData>) VeilShaderBufferRegistry.REGISTRY.get(Veil.veilPath("light_data"));

    private HubbleShaderBufferRegistry() {}

    @ApiStatus.Internal
    public static void bootstrap() {
    }
}
