package me.basiqueevangelist.flashlight.impl.builtin;

import me.basiqueevangelist.flashlight.api.FlashlightEntrypoint;
import me.basiqueevangelist.flashlight.api.module.FlashlightModule;
import net.fabricmc.loader.api.FabricLoader;

import java.util.function.Consumer;

public class BuiltinModules implements FlashlightEntrypoint {
    @Override
    public void registerModules(Consumer<FlashlightModule> moduleRegistry) {
        moduleRegistry.accept(KeyBindingsModule.INSTANCE);
        if (FabricLoader.getInstance().isModLoaded("modmenu")) moduleRegistry.accept(ModConfigModule.INSTANCE);
    }
}
