package me.basiqueevangelist.limelight.impl.resource.wiki;

import me.basiqueevangelist.limelight.impl.builtin.WikiExtension;

import java.util.function.Consumer;

public interface WikiSource {
    String createSearchUrl(String searchText);

    void gatherEntriesFromSearch(String queryBody, String searchText, Consumer<WikiExtension.EntryData> entryConsumer);

    WikiSourceType<?> id();
}
