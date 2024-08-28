package io.wispforest.limelight.impl;

import io.wispforest.limelight.api.entry.ResultEntry;
import io.wispforest.limelight.api.entry.ResultGatherContext;
import io.wispforest.limelight.api.extension.LimelightExtensions;

import java.util.function.Consumer;

public final class ResultGatherer {
    private ResultGatherer() {

    }

    public static void findResults(ResultGatherContext ctx, Consumer<ResultEntry> resultConsumer) {
        for (var extension : LimelightExtensions.enabledExtensions()) {
            var gatherer = extension.checkExclusiveGatherer(ctx);

            if (gatherer != null) {
                gatherer.gatherEntries(ctx, resultConsumer);
                return;
            }
        }

        for (var extension : LimelightExtensions.enabledExtensions()) {
            extension.gatherEntries(ctx, resultConsumer);
        }
    }
}
