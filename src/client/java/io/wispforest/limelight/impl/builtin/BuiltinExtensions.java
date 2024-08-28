package io.wispforest.limelight.impl.builtin;

import io.wispforest.limelight.api.LimelightEntrypoint;
import io.wispforest.limelight.api.extension.LimelightExtension;
import io.wispforest.limelight.impl.builtin.wiki.WikiExtension;
import net.fabricmc.loader.api.FabricLoader;

import java.util.function.Consumer;

public class BuiltinExtensions implements LimelightEntrypoint {
    @Override
    public void registerExtensions(Consumer<LimelightExtension> extensionRegistry) {
        extensionRegistry.accept(KeyBindingsExtension.INSTANCE);
        if (FabricLoader.getInstance().isModLoaded("modmenu")) extensionRegistry.accept(ModConfigExtension.INSTANCE);
        extensionRegistry.accept(CalculatorExtension.INSTANCE);
        if (FabricLoader.getInstance().isModLoaded("lavender")) extensionRegistry.accept(LavenderExtension.INSTANCE);
        extensionRegistry.accept(BangsExtension.INSTANCE);
        extensionRegistry.accept(WikiExtension.INSTANCE);

//        extensionRegistry.accept(GameSettingsExtension.INSTANCE);
    }
}
