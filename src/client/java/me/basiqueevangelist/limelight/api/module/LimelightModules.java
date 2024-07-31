package me.basiqueevangelist.limelight.api.module;

import me.basiqueevangelist.limelight.impl.ModuleManager;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public final class LimelightModules {
    public LimelightModules() {

    }

    /**
     * @return all loaded modules
     * @apiNote This will also return modules that are disabled by the user config.
     */
    public static @UnmodifiableView List<LimelightModule> allModules() {
        return ModuleManager.allModules();
    }

    /**
     * @return all loaded and enabled modules
     */
    public static @UnmodifiableView List<LimelightModule> enabledModules() {
        return ModuleManager.enabledModules();
    }
}
