package me.basiqueevangelist.flashlight.impl;

import me.basiqueevangelist.flashlight.api.entry.ResultEntry;
import me.basiqueevangelist.flashlight.api.module.FlashlightModules;

import java.util.ArrayList;
import java.util.List;

public final class ResultGatherer {
    private ResultGatherer() {

    }

    public static List<ResultEntry> gatherResults(String searchText) {
        List<ResultEntry> results = new ArrayList<>();

        // TODO: only use enabled modules
        for (var module : FlashlightModules.allModules()) {
            var gatherer = module.checkExclusiveGatherer(searchText);

            if (gatherer != null) {
                gatherer.gatherEntries(searchText, results::add);
                return results;
            }
        }

        for (var module : FlashlightModules.allModules()) {
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
