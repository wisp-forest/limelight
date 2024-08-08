package me.basiqueevangelist.limelight.impl.builtin;

import me.basiqueevangelist.limelight.api.action.ResultEntryAction;
import me.basiqueevangelist.limelight.api.action.SetSearchTextResultEntryAction;
import me.basiqueevangelist.limelight.api.builtin.BangsProvider;
import me.basiqueevangelist.limelight.api.entry.ResultEntry;
import me.basiqueevangelist.limelight.api.entry.ResultEntryGatherer;
import me.basiqueevangelist.limelight.api.entry.ResultGatherContext;
import me.basiqueevangelist.limelight.api.module.LimelightModule;
import me.basiqueevangelist.limelight.api.module.LimelightModules;
import me.basiqueevangelist.limelight.impl.Limelight;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BangsModule implements LimelightModule {
    public static final Identifier ID = Limelight.id("bangs");
    public static final BangsModule INSTANCE = new BangsModule();

    private BangsModule() {

    }

    @Override
    public void gatherEntries(ResultGatherContext ctx, Consumer<ResultEntry> entryConsumer) {
        if (!ctx.searchText().startsWith("!")) return;

        Map<String, BangsProvider.Bang> bangs = new HashMap<>();

        for (var module : LimelightModules.enabledModules()) {
            if (!(module instanceof BangsProvider provider)) continue;

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

    private record BangSuggestionEntry(BangsProvider.Bang bang, String remainingText) implements ResultEntry, SetSearchTextResultEntryAction {
        @Override
        public String newSearchText() {
            return "!" + bang.key() + " " + remainingText;
        }

        @Override
        public LimelightModule module() {
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

        @Override
        public ResultEntryAction action() {
            return this;
        }
    }
}
