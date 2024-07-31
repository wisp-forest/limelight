package me.basiqueevangelist.flashlight.api;

import me.basiqueevangelist.flashlight.api.module.FlashlightModule;

import java.util.function.Consumer;

public interface FlashlightEntrypoint {
    String KEY = "flashlight";

    void registerModules(Consumer<FlashlightModule> moduleRegistry);
}
