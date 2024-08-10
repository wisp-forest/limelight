package me.basiqueevangelist.limelight.api.module;

import me.basiqueevangelist.limelight.api.entry.ResultEntry;
import me.basiqueevangelist.limelight.api.entry.ResultGatherContext;
import me.basiqueevangelist.limelight.api.entry.ResultEntryGatherer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Base interface for extending Limelight.
 */
public interface LimelightModule extends ResultEntryGatherer {
    /**
     * @return the unique identifier of this specific Limelight module
     */
    Identifier id();

    /**
     * @return the name of this module, to be used in the main and config screens
     */
    default Text name() {
        return Text.translatable(Util.createTranslationKey("limelightModule", id()));
    }

    /**
     * @return a list of texts to be used as a tooltip describing this module
     */
    default List<Text> tooltip() {
        List<Text> tooltip = new ArrayList<>();

        tooltip.add(name().copy().formatted(Formatting.BOLD));

        var desc = description();
        if (desc != null) {
            tooltip.add(desc);
        }

        if (MinecraftClient.getInstance().options.advancedItemTooltips) {
            tooltip.add(Text.literal(id().toString()).formatted(Formatting.DARK_GRAY));
        }

        return tooltip;
    }

    /**
     * @return the description of this module, to be used in the main and config screens
     */
    default @Nullable Text description() {
        String key = Util.createTranslationKey("limelightModule", id()) + ".desc";
        if (!Language.getInstance().hasTranslation(key)) return null;
        return Text.translatable(key);
    }

    default @Nullable ResultEntryGatherer checkExclusiveGatherer(ResultGatherContext ctx) {
        return null;
    }

    @Override
    default void gatherEntries(ResultGatherContext ctx, Consumer<ResultEntry> entryConsumer) { }
}
