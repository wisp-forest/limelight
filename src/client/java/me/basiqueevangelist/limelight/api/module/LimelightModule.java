package me.basiqueevangelist.limelight.api.module;

import me.basiqueevangelist.limelight.api.entry.ResultEntry;
import me.basiqueevangelist.limelight.api.entry.ResultEntryGatherer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

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
     * @return the description of this module, to be used in the main and config screens
     */
    default @Nullable Text description() {
        String key = Util.createTranslationKey("limelightModule", id()) + ".desc";
        if (!Language.getInstance().hasTranslation(key)) return null;
        return Text.translatable(key);
    }

    default @Nullable ResultEntryGatherer checkExclusiveGatherer(String searchText) {
        return null;
    }

    @Override
    default void gatherEntries(String searchText, Consumer<ResultEntry> entryConsumer) { }
}
