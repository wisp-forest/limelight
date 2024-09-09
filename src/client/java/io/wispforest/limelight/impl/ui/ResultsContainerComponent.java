package io.wispforest.limelight.impl.ui;

import io.wispforest.limelight.api.entry.ExpandableResultEntry;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.limelight.api.entry.ResultEntry;
import io.wispforest.limelight.api.entry.ResultGatherContext;
import io.wispforest.limelight.impl.Limelight;
import io.wispforest.limelight.impl.ResultGatherer;
import net.minecraft.client.MinecraftClient;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class ResultsContainerComponent extends FlowLayout {
    private final LimelightScreen screen;
    private List<ResultEntry> results = new ArrayList<>();
    private boolean hasInitialized = false;
    private volatile boolean sentReload = false;
    private Map<String,Boolean> expanded = new HashMap<>();

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

    void toggleExpanded(String id) {
        expanded.put(id, !expanded.getOrDefault(id, false));
        MinecraftClient.getInstance().send(this::rebuildContents);
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
                boolean isExpanded = entry instanceof ExpandableResultEntry && expanded.getOrDefault(entry.entryId(), false);
                var result = new ResultEntryComponent(screen, entry, false, isExpanded);

                if (screen.firstResult == null) screen.firstResult = result;

                child(result);

                if (isExpanded) {
                    for (ResultEntry e : ((ExpandableResultEntry)entry).getChildren())
                        child(new ResultEntryComponent(screen, e, true, false));
                }
            }
        });

        if (parent != null) queue(this::updateLayout);
        sentReload = false;
    }
}
