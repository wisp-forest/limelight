package me.basiqueevangelist.flashlight.api.module;

import me.basiqueevangelist.flashlight.impl.ModuleManager;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public final class FlashlightModules {
    public FlashlightModules() {

    }

    public static @UnmodifiableView List<FlashlightModule> allModules() {
        return ModuleManager.allModules();
    }
}
