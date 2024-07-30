package me.basiqueevangelist.flashlight.impl;

import me.basiqueevangelist.flashlight.api.ResultEntry;
import me.basiqueevangelist.flashlight.api.ResultGatherEvents;

import java.util.ArrayList;
import java.util.List;

public final class ResultGatherer {
    private ResultGatherer() {

    }

    public static List<ResultEntry> gatherResults(String searchText) {
        List<ResultEntry> results = new ArrayList<>();

        if (ResultGatherEvents.EXCLUSIVE_GATHER.invoker().onExclusiveEntryGather(searchText, results::add))
            return results;

        ResultGatherEvents.GATHER.invoker().onEntryGather(searchText, results::add);

        return results;
    }
}
