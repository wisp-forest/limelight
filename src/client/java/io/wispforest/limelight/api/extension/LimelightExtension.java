package io.wispforest.limelight.api.extension;

import io.wispforest.limelight.api.entry.ResultEntry;
import io.wispforest.limelight.api.entry.ResultGatherContext;
import io.wispforest.limelight.api.entry.ResultEntryGatherer;
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
 * An extension of Limelight functionality.
 */
public interface LimelightExtension extends ResultEntryGatherer {
    /**
     * @return the unique identifier of this specific Limelight extension
     */
    Identifier id();

    /**
     * @return the base translation key
     * */
    default String baseTranslationKey() {
        return Util.createTranslationKey("limelightExtension", id());
    }

    /**
     * @return the name of this extension, to be used in the main and config screens
     */
    default Text name() {
        return Text.translatable(baseTranslationKey());
    }

    /**
     * @return a list of texts to be used as a tooltip describing this extension
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
     * @return the description of this extension, to be used in the main and config screens
     */
    default @Nullable Text description() {
        String key = baseTranslationKey() + ".desc";
        if (!Language.getInstance().hasTranslation(key)) return null;
        return Text.translatable(key);
    }

    /**
     * Check if this extension will exclusively handle this search.
     * @param ctx the result gathering context
     * @return a result entry gatherer if this request should be handled exclusively, {@code null} otherwise
     */
    default @Nullable ResultEntryGatherer checkExclusiveGatherer(ResultGatherContext ctx) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default void gatherEntries(ResultGatherContext ctx, Consumer<ResultEntry> entryConsumer) { }
}
