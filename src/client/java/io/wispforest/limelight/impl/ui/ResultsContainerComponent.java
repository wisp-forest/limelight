package io.wispforest.limelight.impl.ui;

import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.limelight.api.entry.ResultEntry;
import io.wispforest.limelight.api.entry.ResultGatherContext;
import io.wispforest.limelight.impl.Limelight;
import io.wispforest.limelight.impl.ResultGatherer;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class ResultsContainerComponent extends FlowLayout {
    private final LimelightScreen screen;
    private List<ResultEntry> results = new ArrayList<>();
    private boolean hasInitialized = false;
    private volatile boolean sentReload = false;

    public ResultsContainerComponent(LimelightScreen screen, ResultGatherContext ctx) {
        super(Sizing.fill(), Sizing.content(), Algorithm.VERTICAL);
        this.screen = screen;

        Consumer<ResultEntry> resultConsumer = result -> {
            if (ctx.cancellationToken().cancelled()) return;

            results.add(result);

            if (hasInitialized) {
                if (!sentReload) {
                    sentReload = true;
                    MinecraftClient.getInstance().send(this::rebuildContents);
                }
            }
        };

        ResultGatherer.findResults(ctx, resultConsumer);

        this.hasInitialized = true;
        this.results = new CopyOnWriteArrayList<>(this.results);
        rebuildContents();
    }

    private void rebuildContents() {
        results.sort(Comparator.comparing(entry -> {
            var id = entry.entryId();
            var position = Limelight.ENTRY_USES.indexOf(id);

            if (position == -1) return Limelight.ENTRY_USES.capacity();
            else return position;
        }));

        configure(ignored -> {
            screen.firstResult = null;
            clearChildren();

            for (var entry : results) {
                var result = new ResultEntryComponent(screen, entry);

                if (screen.firstResult == null) screen.firstResult = result;

                child(result);
            }
        });

        if (parent != null) queue(this::updateLayout);
        sentReload = false;
    }
}
