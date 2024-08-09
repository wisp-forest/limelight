package me.basiqueevangelist.limelight.impl.builtin;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import me.basiqueevangelist.limelight.api.action.InvokeAction;
import me.basiqueevangelist.limelight.api.action.ResultAction;
import me.basiqueevangelist.limelight.api.builtin.BangsProvider;
import me.basiqueevangelist.limelight.api.entry.ResultEntry;
import me.basiqueevangelist.limelight.api.entry.ResultGatherContext;
import me.basiqueevangelist.limelight.api.module.LimelightModule;
import me.basiqueevangelist.limelight.impl.Limelight;
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

public class WikiModule implements LimelightModule, BangsProvider {
    public static final Identifier ID = Limelight.id("wiki");
    public static final WikiModule INSTANCE = new WikiModule();

    private static final HttpClient CLIENT = HttpClient.newBuilder()
        .executor(Util.getDownloadWorkerExecutor())
        .build();

    private WikiModule() { }

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public void gatherEntries(ResultGatherContext ctx, Consumer<ResultEntry> entryConsumer) {
        for (var wiki : WikiLoader.WIKIS.values()) {
            gatherEntriesForWiki(ctx, entryConsumer, wiki);
        }
    }

    private void gatherEntriesForWiki(ResultGatherContext ctx, Consumer<ResultEntry> entryConsumer, WikiLoader.WikiDescription wiki) {
        // TODO: make debounce configurable
        new CompletableFuture<Void>()
            .completeOnTimeout(null, 100, TimeUnit.MILLISECONDS)
            .thenCompose(ignored -> {
                ctx.cancellationToken().throwIfCancelled();

                var request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(wiki.openSearchUrl(ctx.searchText())))
                    .header("User-Agent", getUserAgent())
                    .build();

                return ctx.cancellationToken().wrapFuture(CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString()));
            })
            .thenAccept(res -> {
                ctx.cancellationToken().throwIfCancelled();

                if (res.statusCode() > 299) return; // TODO: log?

                var json = JsonParser.parseString(res.body()).getAsJsonArray();

                JsonArray titles = json.get(1).getAsJsonArray();
                JsonArray urls = json.get(3).getAsJsonArray();

                for (int idx = 0; idx < titles.size(); idx++) {
                    entryConsumer.accept(new WikiSearchResultEntry(wiki, titles.get(idx).getAsString(), urls.get(idx).getAsString()));
                }
            });
    }

    private static String getUserAgent() {
        String limelightVersion = FabricLoader.getInstance().getModContainer("limelight").orElseThrow().getMetadata().getVersion().getFriendlyString();
        String mcVersion = SharedConstants.getGameVersion().getId();

        return "Limelight/" + limelightVersion + " (https://github.com/BasiqueEvangelist/Limelight) Minecraft/" + mcVersion;
    }

    @Override
    public List<Bang> bangs() {
        List<Bang> bangs = new ArrayList<>();

        for (var wiki : WikiLoader.WIKIS.values()) {
            if (wiki.bangKey() != null)
                bangs.add(new Bang(wiki.bangKey(), wiki.title(), (ctx, entryConsumer) -> gatherEntriesForWiki(ctx, entryConsumer, wiki)));
        }

        return bangs;
    }

    private record WikiSearchResultEntry(WikiLoader.WikiDescription wiki, String title, String url) implements ResultEntry, InvokeAction {
        @Override
        public void run() {
            Util.getOperatingSystem().open(url);
        }

        @Override
        public LimelightModule module() {
            return INSTANCE;
        }

        @Override
        public String entryId() {
            return "limelight:wiki/'" + wiki.mediaWikiApi() + "'/" + title;
        }

        @Override
        public Text text() {
            return Text.empty()
                .append(wiki.title())
                .append(" > ")
                .append(title);
        }

        @Override
        public ResultAction action() {
            return this;
        }
    }
}
