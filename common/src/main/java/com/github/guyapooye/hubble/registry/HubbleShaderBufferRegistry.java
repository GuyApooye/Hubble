package com.github.guyapooye.hubble.registry;

import com.github.guyapooye.hubble.client.render.shader.block.AtmosphereData;
import com.github.guyapooye.hubble.client.render.shader.block.SunData;
import com.github.guyapooye.hubble.client.render.shader.block.PlanetData;
import foundry.veil.api.client.render.VeilShaderBufferLayout;
import foundry.veil.platform.registry.RegistrationProvider;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

import static foundry.veil.api.client.registry.VeilShaderBufferRegistry.REGISTRY_KEY;

public final class HubbleShaderBufferRegistry {
    private static final RegistrationProvider<VeilShaderBufferLayout<?>> PROVIDER;
    public final static Supplier<VeilShaderBufferLayout<PlanetData>> PLANET_DATA;
    public final static Supplier<VeilShaderBufferLayout<AtmosphereData>> ATMOSPHERE_DATA;
    public final static Supplier<VeilShaderBufferLayout<SunData>> LIGHT_DATA;

    private HubbleShaderBufferRegistry() {}

    @ApiStatus.Internal
    public static void bootstrap() {
    }

    private static <T> Supplier<VeilShaderBufferLayout<T>> register(String name, Supplier<VeilShaderBufferLayout<T>> layout) {
        return PROVIDER.register(name, layout);
    }

    static {
        PROVIDER = RegistrationProvider.get(REGISTRY_KEY, "hubble");
        PLANET_DATA = register("planet_data", PlanetData::createLayout);
        ATMOSPHERE_DATA = register("atmosphere_data", AtmosphereData::createLayout);
        LIGHT_DATA = register("sun_data", SunData::createLayout);
    }
}
