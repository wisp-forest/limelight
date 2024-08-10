package me.basiqueevangelist.limelight.impl.config;

import blue.endless.jankson.Comment;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.SectionHeader;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

@Config(name = "limelight", wrapperName = "LimelightConfig")
public class LimelightConfigModel {
    @Comment("If true, the Limelight screen will pause the game while in singleplayer")
    public boolean pauseGameWhileInScreen = false;

    @SectionHeader("modules")
    public Map<Identifier, Boolean> enabledModules = new HashMap<>();
}
