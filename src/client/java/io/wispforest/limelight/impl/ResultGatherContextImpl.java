package io.wispforest.limelight.impl;

import io.wispforest.owo.util.Observable;
import io.wispforest.limelight.api.entry.ResultGatherContext;
import io.wispforest.limelight.api.util.CancellationToken;
import io.wispforest.limelight.impl.util.ReactiveUtils;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

public class ResultGatherContextImpl implements ResultGatherContext {
    private static final Logger LOGGER = LoggerFactory.getLogger("Limelight/ResultGatherContextImpl");

    private final String searchText;
    private final List<SearchWord> searchWords;
    private final CancellationToken cancellationToken;

    private final Observable<Integer> inProgress = Observable.of(0);
    private final Observable<Boolean> hasFinished = ReactiveUtils.map(inProgress, count -> count == 0);

    public ResultGatherContextImpl(String searchText, CancellationToken cancellationToken) {
        this.searchText = searchText;
        this.searchWords = SearchWord.parseSearch(searchText);
        this.cancellationToken = cancellationToken;
    }

    @Override
    public String searchText() {
        return searchText;
    }

    @Override
    public ResultGatherContext withSearchText(String searchText) {
        return new ResultGatherContextImpl(searchText, cancellationToken);
    }

    @Override
    public boolean matches(String... parts) {
        outer: for (var word : searchWords) {
            for (var part : parts) {
                TriState state = word.matches(part);

                if (state == TriState.FALSE) return false;
                if (state == TriState.TRUE) continue outer;
            }

            return false;
        }

        return true;
    }

    @Override
    public void trackFuture(CompletableFuture<?> future) {
        cancellationToken.wrapFuture(future);

        synchronized (this) {
            inProgress.set(inProgress.get() + 1);
        }

        future.whenComplete((ignored, e) -> {
            synchronized (this) {
                inProgress.set(inProgress.get() - 1);
            }

            if (e instanceof CancellationException) return;

            if (e != null) {
                LOGGER.error("Asynchronous task for search failed", e);
            }
        });
    }

    @Override
    public CancellationToken cancellationToken() {
        return cancellationToken;
    }

    @Override
    public ClientPlayerEntity player() {
        return MinecraftClient.getInstance().player;
    }

    @Override
    public MinecraftClient client() {
        return MinecraftClient.getInstance();
    }

    private record SearchWord(String match, boolean inverted) {
        public TriState matches(String text) {
            return StringUtils.containsIgnoreCase(text, match) ? TriState.of(!inverted) : TriState.DEFAULT;
        }

        public static List<SearchWord> parseSearch(String searchText) {
            if (searchText.isEmpty()) return List.of();

            List<SearchWord> words = new ArrayList<>();

            for (String word : searchText.split(" ")) {
                if (word.startsWith("!"))
                    words.add(new SearchWord(word.substring(1), true));
                else
                    words.add(new SearchWord(word, false));
            }

            return words;
        }
    }
}
