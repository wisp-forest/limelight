package io.wispforest.limelight.impl.config;

import blue.endless.jankson.Comment;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Hook;
import io.wispforest.owo.config.annotation.RangeConstraint;
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

    @Comment("The theme used in the Limelight GUI")
    public Theme theme = Theme.LIGHT;

    @Comment("If true, will show a count of the results next to the search bar")
    public boolean showResultCounter = false;

    @Comment("Percent of screen width used by the Limelight popup")
    @RangeConstraint(min = 0, max = 100) public int horizontalPercent = 40;

    @Comment("Percent of screen height used by the Limelight popup")
    @RangeConstraint(min = 0, max = 100) public int verticalPercent = 50;

    @Hook
    @SectionHeader("extensions")
    public Map<Identifier, Boolean> enabledExtensions = new HashMap<>();

    public enum Theme {
        LIGHT,
        DARK
    }
}
