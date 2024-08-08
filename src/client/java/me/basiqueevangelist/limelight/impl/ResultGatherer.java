package me.basiqueevangelist.limelight.impl;

import me.basiqueevangelist.limelight.api.entry.ResultEntry;
import me.basiqueevangelist.limelight.api.entry.ResultGatherContext;
import me.basiqueevangelist.limelight.api.module.LimelightModules;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ResultGatherer {
    private ResultGatherer() {

    }

    public static List<ResultEntry> gatherResults(ResultGatherContext ctx) {
        var results = findResults(ctx);

        results.sort(Comparator.comparing(entry -> {
            var id = entry.entryId();
            var position = Limelight.ENTRY_USES.indexOf(id);

            if (position == -1) return Limelight.ENTRY_USES.capacity();
            else return position;
        }));

        return results;
    }

    public static List<ResultEntry> findResults(ResultGatherContext ctx) {
        List<ResultEntry> results = new ArrayList<>();

        for (var module : LimelightModules.enabledModules()) {
            var gatherer = module.checkExclusiveGatherer(ctx);

            if (gatherer != null) {
                gatherer.gatherEntries(ctx, results::add);
                return results;
            }
        }

        for (var module : LimelightModules.enabledModules()) {
            module.gatherEntries(ctx, results::add);
        }

        return results;
    }
}
