package me.basiqueevangelist.limelight.api.module;

import me.basiqueevangelist.limelight.impl.ModuleManager;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public final class LimelightModules {
    public LimelightModules() {

    }

    public static @UnmodifiableView List<LimelightModule> allModules() {
        return ModuleManager.allModules();
    }
}
