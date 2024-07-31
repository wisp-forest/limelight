package me.basiqueevangelist.limelight.impl;

import me.basiqueevangelist.limelight.api.entry.ResultEntry;
import me.basiqueevangelist.limelight.api.module.LimelightModules;

import java.util.ArrayList;
import java.util.List;

public final class ResultGatherer {
    private ResultGatherer() {

    }

    public static List<ResultEntry> gatherResults(String searchText) {
        List<ResultEntry> results = new ArrayList<>();

        for (var module : LimelightModules.enabledModules()) {
            var gatherer = module.checkExclusiveGatherer(searchText);

            if (gatherer != null) {
                gatherer.gatherEntries(searchText, results::add);
                return results;
            }
        }

        for (var module : LimelightModules.enabledModules()) {
            module.gatherEntries(searchText, results::add);
            var gatherer = module.checkExclusiveGatherer(searchText);

            if (gatherer != null) {
                gatherer.gatherEntries(searchText, results::add);
                return results;
            }
        }

        return results;
    }
}
