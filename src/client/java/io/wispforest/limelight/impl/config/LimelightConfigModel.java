package io.wispforest.limelight.impl.config;

import blue.endless.jankson.Comment;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Hook;
import io.wispforest.owo.config.annotation.SectionHeader;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

@Config(name = "limelight", wrapperName = "LimelightConfig")
public class LimelightConfigModel {
    @Comment("If true, the Limelight screen will pause the game while in singleplayer")
    public boolean pauseGameWhileInScreen = false;

    @Comment("Allows you to search wikis without specifying a bang")
    public boolean implicitWikiSearch = true;

    @Hook
    @SectionHeader("extensions")
    public Map<Identifier, Boolean> enabledExtensions = new HashMap<>();
}
