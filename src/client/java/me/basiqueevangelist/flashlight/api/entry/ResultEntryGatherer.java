package me.basiqueevangelist.flashlight.api.entry;

import java.util.function.Consumer;

public interface ResultEntryGatherer {
    void gatherEntries(String searchText, Consumer<ResultEntry> entryConsumer);
}
