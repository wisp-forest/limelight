package me.basiqueevangelist.limelight.impl;

import me.basiqueevangelist.limelight.api.entry.ResultGatherContext;
import me.basiqueevangelist.limelight.impl.util.CancellationToken;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ResultGatherContextImpl implements ResultGatherContext {
    private final String searchText;
    private final List<SearchWord> searchWords;
    private final CancellationToken cancellationToken;

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
