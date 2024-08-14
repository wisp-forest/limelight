package me.basiqueevangelist.limelight.api;

import me.basiqueevangelist.limelight.api.extension.LimelightExtension;

import java.util.function.Consumer;

public interface LimelightEntrypoint {
    String KEY = "limelight";

    void registerExtensions(Consumer<LimelightExtension> extensionRegistry);
}
