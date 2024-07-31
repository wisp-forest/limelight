package me.basiqueevangelist.limelight.impl.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.basiqueevangelist.limelight.impl.config.LimelightConfigScreen;

public class LimelightModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return LimelightConfigScreen::generateScreen;
    }
}
