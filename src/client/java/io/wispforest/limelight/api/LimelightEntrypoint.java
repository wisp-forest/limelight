package io.wispforest.limelight.api;

import io.wispforest.limelight.api.extension.LimelightExtension;

import java.util.function.Consumer;

public interface LimelightEntrypoint {
    String KEY = "limelight";

    void registerExtensions(Consumer<LimelightExtension> extensionRegistry);
}
