package me.basiqueevangelist.flashlight.api.entry;

import java.util.function.Consumer;

public interface ResultEntryGatherer {
    /**
     * Gathers and provides results for a search request.
     * @param searchText the text the user typed in
     * @param entryConsumer a consumer that the gatherer should give the results to
     */
    void gatherEntries(String searchText, Consumer<ResultEntry> entryConsumer);
}
