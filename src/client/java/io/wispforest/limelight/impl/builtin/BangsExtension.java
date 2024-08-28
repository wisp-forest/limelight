package io.wispforest.limelight.impl.builtin;

import io.wispforest.limelight.api.entry.SetSearchTextEntry;
import io.wispforest.limelight.api.builtin.bangs.BangDefinition;
import io.wispforest.limelight.api.builtin.bangs.BangsProvider;
import io.wispforest.limelight.api.entry.ResultEntry;
import io.wispforest.limelight.api.entry.ResultEntryGatherer;
import io.wispforest.limelight.api.entry.ResultGatherContext;
import io.wispforest.limelight.api.extension.LimelightExtension;
import io.wispforest.limelight.api.extension.LimelightExtensions;
import io.wispforest.limelight.impl.Limelight;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BangsExtension implements LimelightExtension {
    public static final Identifier ID = Limelight.id("bangs");
    public static final BangsExtension INSTANCE = new BangsExtension();

    private BangsExtension() {

    }

    @Override
    public void gatherEntries(ResultGatherContext ctx, Consumer<ResultEntry> entryConsumer) {
        if (!ctx.searchText().startsWith("!")) return;

        Map<String, BangDefinition> bangs = new HashMap<>();

        for (var extension : LimelightExtensions.enabledExtensions()) {
            if (!(extension instanceof BangsProvider provider)) continue;

            for (var bang : provider.bangs()) {
                // TODO: check for duplicates
                bangs.put(bang.key(), bang);
            }
        }

        int spaceIdx = ctx.searchText().indexOf(' ');
        String bangKey = ctx.searchText().substring(1, spaceIdx == -1 ? ctx.searchText().length() : spaceIdx);
        String remainingText = spaceIdx == -1 ? "" : ctx.searchText().substring(spaceIdx + 1);

        var bang = bangs.get(bangKey);
        if (bang != null) {
            bang.gatherer().gatherEntries(ctx.withSearchText(remainingText), entryConsumer);
            return;
        }

        for (var possible : bangs.values()) {
            var entry = new BangSuggestionEntry(possible, remainingText);
            if (!StringUtils.containsIgnoreCase(possible.key(), bangKey)) continue;
            entryConsumer.accept(entry);
        }
    }

    @Override
    public @Nullable ResultEntryGatherer checkExclusiveGatherer(ResultGatherContext ctx) {
        if (!ctx.searchText().startsWith("!")) return null;

        return this;
    }

    @Override
    public Identifier id() {
        return ID;
    }

    private record BangSuggestionEntry(BangDefinition bang, String remainingText) implements SetSearchTextEntry {
        @Override
        public String newSearchText() {
            return "!" + bang.key() + " " + remainingText;
        }

        @Override
        public LimelightExtension extension() {
            return INSTANCE;
        }

        @Override
        public String entryId() {
            return "limelight:bang/" + bang;
        }

        @Override
        public Text text() {
            return Text.empty()
                .append(bang.title())
                .append(" (!" + bang.key() + ")");
        }
    }
}
