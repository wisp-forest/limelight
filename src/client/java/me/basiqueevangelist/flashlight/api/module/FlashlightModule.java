package me.basiqueevangelist.flashlight.api.module;

import me.basiqueevangelist.flashlight.api.entry.ResultEntry;
import me.basiqueevangelist.flashlight.api.entry.ResultEntryGatherer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Base interface for extending Flashlight.
 */
public interface FlashlightModule extends ResultEntryGatherer {
    Identifier id();

    default Text name() {
        return Text.translatable(Util.createTranslationKey("flashlightModule", id()));
    }

    default @Nullable ResultEntryGatherer checkExclusiveGatherer(String searchText) {
        return null;
    }

    @Override
    default void gatherEntries(String searchText, Consumer<ResultEntry> entryConsumer) { }
}
