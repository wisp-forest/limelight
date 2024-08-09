package me.basiqueevangelist.limelight.impl;

import me.basiqueevangelist.limelight.api.entry.ResultEntry;
import me.basiqueevangelist.limelight.api.entry.ResultGatherContext;
import me.basiqueevangelist.limelight.api.module.LimelightModules;

import java.util.function.Consumer;

public final class ResultGatherer {
    private ResultGatherer() {

    }

    public static void findResults(ResultGatherContext ctx, Consumer<ResultEntry> resultConsumer) {
        for (var module : LimelightModules.enabledModules()) {
            var gatherer = module.checkExclusiveGatherer(ctx);

            if (gatherer != null) {
                gatherer.gatherEntries(ctx, resultConsumer);
                return;
            }
        }

        for (var module : LimelightModules.enabledModules()) {
            module.gatherEntries(ctx, resultConsumer);
        }
    }
}
