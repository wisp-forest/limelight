package me.basiqueevangelist.limelight.api;

import me.basiqueevangelist.limelight.api.module.LimelightModule;

import java.util.function.Consumer;

public interface LimelightEntrypoint {
    String KEY = "limelight";

    void registerModules(Consumer<LimelightModule> moduleRegistry);
}
