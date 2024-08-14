package me.basiqueevangelist.limelight.impl;

import me.basiqueevangelist.limelight.api.entry.ResultEntry;
import me.basiqueevangelist.limelight.api.entry.ResultGatherContext;
import me.basiqueevangelist.limelight.api.extension.LimelightExtensions;

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
