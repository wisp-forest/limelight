package me.basiqueevangelist.limelight.impl.builtin;

import me.basiqueevangelist.limelight.api.LimelightEntrypoint;
import me.basiqueevangelist.limelight.api.module.LimelightModule;
import net.fabricmc.loader.api.FabricLoader;

import java.util.function.Consumer;

public class BuiltinModules implements LimelightEntrypoint {
    @Override
    public void registerModules(Consumer<LimelightModule> moduleRegistry) {
        moduleRegistry.accept(KeyBindingsModule.INSTANCE);
        if (FabricLoader.getInstance().isModLoaded("modmenu")) moduleRegistry.accept(ModConfigModule.INSTANCE);
        moduleRegistry.accept(CalculatorModule.INSTANCE);
        if (FabricLoader.getInstance().isModLoaded("lavender")) moduleRegistry.accept(LavenderModule.INSTANCE);
        moduleRegistry.accept(BangsModule.INSTANCE);

//        moduleRegistry.accept(GameSettingsModule.INSTANCE);
    }
}
