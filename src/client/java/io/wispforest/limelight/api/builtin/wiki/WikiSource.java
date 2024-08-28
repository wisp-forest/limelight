package io.wispforest.limelight.api.builtin.wiki;

import java.util.function.Consumer;

/**
 * A source of wiki search results to be used in the Wiki extension.
 */
public interface WikiSource {
    /**
     * Provides a URL for a resource this wiki source will use to search.
     * <p>
     * For example, if your wiki has a search index, you can unconditionally return it.
     * If your wiki has a search API instead, you should return that search API's endpoint specialized for this search
     * text.
     *
     * @param searchText the text to search for
     * @return the needed URL
     */
    String createSearchUrl(String searchText);

    /**
     * Gathers search results for this search text.
     * @param queryBody the contents of the resource referred to by {@link #createSearchUrl(String)}
     * @param searchText the text to search for
     * @param entryConsumer a consumer that accepts found search entries
     */
    void gatherEntriesFromSearch(String queryBody, String searchText, Consumer<EntryData> entryConsumer);

    /**
     * @return the type of this wiki source
     */
    WikiSourceType<?> type();

    /**
     * A prepared wiki search result.
     * @param title the result's human-readable title
     * @param url the URL to navigate to when opening it
     */
    record EntryData(String title, String url) { }
}
