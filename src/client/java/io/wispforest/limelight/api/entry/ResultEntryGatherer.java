package io.wispforest.limelight.api.entry;

import java.util.function.Consumer;

public interface ResultEntryGatherer {
    /**
     * Gathers and provides results for a search request.
     *
     * @param ctx the result gathering context
     * @param entryConsumer a consumer that the gatherer should give the results to
     */
    void gatherEntries(ResultGatherContext ctx, Consumer<ResultEntry> entryConsumer);
}
