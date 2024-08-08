package me.basiqueevangelist.limelight.api.entry;

import java.util.function.Consumer;

public interface ResultEntryGatherer {
    /**
     * Gathers and provides results for a search request.
     * @param entryConsumer a consumer that the gatherer should give the results to
     */
    void gatherEntries(ResultGatherContext ctx, Consumer<ResultEntry> entryConsumer);
}
