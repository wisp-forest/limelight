package me.basiqueevangelist.limelight.impl.builtin;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.basiqueevangelist.limelight.api.entry.InvokeResultEntry;
import me.basiqueevangelist.limelight.api.builtin.bangs.BangDefinition;
import me.basiqueevangelist.limelight.api.builtin.bangs.BangsProvider;
import me.basiqueevangelist.limelight.api.entry.ResultEntry;
import me.basiqueevangelist.limelight.api.entry.ResultGatherContext;
import me.basiqueevangelist.limelight.api.extension.LimelightExtension;
import me.basiqueevangelist.limelight.impl.Limelight;
import me.basiqueevangelist.limelight.impl.resource.wiki.WikiDescription;
import me.basiqueevangelist.limelight.impl.resource.WikiLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class WikiExtension implements LimelightExtension, BangsProvider {
    public static final Identifier ID = Limelight.id("wiki");
    public static final WikiExtension INSTANCE = new WikiExtension();

    private static final HttpClient CLIENT = HttpClient.newBuilder()
        .executor(Util.getDownloadWorkerExecutor())
        .build();

    private static final Cache<URI, String> REQUEST_CACHE = CacheBuilder.newBuilder()
        .expireAfterAccess(10, TimeUnit.MINUTES)
        .softValues()
        .maximumSize(200)
        .build();

    private WikiExtension() { }

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public void gatherEntries(ResultGatherContext ctx, Consumer<ResultEntry> entryConsumer) {
        for (var entry : WikiLoader.WIKIS.entrySet()) {
            gatherEntriesForWiki(ctx, entryConsumer, entry.getKey(), entry.getValue());
        }
    }

    private void gatherEntriesForWiki(ResultGatherContext ctx, Consumer<ResultEntry> entryConsumer, Identifier wikiId, WikiDescription<?> wiki) {
        var searchUri = URI.create(wiki.createSearchUrl(ctx.searchText()));
        CompletableFuture<String> dataFuture;
        String possibleData = REQUEST_CACHE.getIfPresent(searchUri);

        if (possibleData == null) {
            dataFuture = new CompletableFuture<Void>()
                .completeOnTimeout(null, 100, TimeUnit.MILLISECONDS)
                .thenCompose(ignored -> {
                    ctx.cancellationToken().throwIfCancelled();

                    var request = HttpRequest.newBuilder()
                        .GET()
                        .uri(searchUri)
                        .header("User-Agent", getUserAgent())
                        .build();

                    return ctx.cancellationToken().wrapFuture(CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString()));
                })
                .thenApply(res -> {
                    if (res.statusCode() > 299) throw new IllegalStateException("Error status code " + res.statusCode()); // TODO: log?

                    var body = res.body();

                    REQUEST_CACHE.put(searchUri, body);

                    return body;
                });
        } else {
            dataFuture = CompletableFuture.completedFuture(possibleData);
        }

        dataFuture.thenAccept(body -> wiki.source().gatherEntriesFromSearch(body, ctx.searchText(),
                data -> entryConsumer.accept(new WikiSearchResultEntry(wikiId, wiki, data))));
    }

    private static String getUserAgent() {
        String limelightVersion = FabricLoader.getInstance().getModContainer("limelight").orElseThrow().getMetadata().getVersion().getFriendlyString();
        String mcVersion = SharedConstants.getGameVersion().getId();

        return "Limelight/" + limelightVersion + " (https://github.com/BasiqueEvangelist/Limelight) Minecraft/" + mcVersion;
    }

    @Override
    public List<BangDefinition> bangs() {
        List<BangDefinition> bangs = new ArrayList<>();

        for (var entry : WikiLoader.WIKIS.entrySet()) {
            var wiki = entry.getValue();
            if (wiki.bangKey() != null)
                bangs.add(new BangDefinition(wiki.bangKey(), wiki.title(), (ctx, entryConsumer) -> gatherEntriesForWiki(ctx, entryConsumer, entry.getKey(), wiki)));
        }

        return bangs;
    }

    public record EntryData(String title, String url) {

    }

    private record WikiSearchResultEntry(Identifier id, WikiDescription<?> wiki, EntryData data) implements InvokeResultEntry {
        @Override
        public void run() {
            Util.getOperatingSystem().open(data.url);
        }

        @Override
        public LimelightExtension extension() {
            return INSTANCE;
        }

        @Override
        public String entryId() {
            return "limelight:wiki/'" + id + "'/" + data.title;
        }

        @Override
        public Text text() {
            return Text.empty()
                .append(wiki.title())
                .append(" > ")
                .append(data.title);
        }
    }
}
