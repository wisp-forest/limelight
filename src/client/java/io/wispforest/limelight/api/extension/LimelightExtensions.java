package io.wispforest.limelight.api.extension;

import io.wispforest.limelight.impl.ExtensionManager;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public final class LimelightExtensions {
    public LimelightExtensions() {

    }

    /**
     * @return all loaded extensions, including disabled ones
     */
    public static @UnmodifiableView List<LimelightExtension> allExtensions() {
        return ExtensionManager.allExtensions();
    }

    /**
     * @return all loaded and enabled extensions
     */
    public static @UnmodifiableView List<LimelightExtension> enabledExtensions() {
        return ExtensionManager.enabledExtensions();
    }
}
