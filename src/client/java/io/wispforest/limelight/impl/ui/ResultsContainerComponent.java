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
    private final Map<ExpandableResultEntry,Integer> expanded = new HashMap<>();

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

    void toggleExpanded(ResultEntryComponent component, ExpandableResultEntry resultEntry) {
        MinecraftClient.getInstance().send( () -> {
            int length = expanded.getOrDefault(resultEntry, 0);
            int index = children.indexOf(component);
            if (length == 0) {
                if (index >= 0) {
                    List<ResultEntry> childEntries = resultEntry.children();
                    length = childEntries.size();
                    for (int i = 0; i < length; i++)
                        child(index+1+i, new ResultEntryComponent(screen, childEntries.get(i), true, false));
                }
                expanded.put(resultEntry, length);
            }
            else {
                for (int i = 0; i < length; i++)
                    removeChild(children.get(index+1));
                expanded.put(resultEntry, 0);
            }
        } );
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
                boolean isExpanded = entry instanceof ExpandableResultEntry && expanded.getOrDefault(entry.entryId(), 0) > 0;
                var result = new ResultEntryComponent(screen, entry, false, isExpanded);

                if (screen.firstResult == null) screen.firstResult = result;

                child(result);

            }
        });

        if (parent != null) queue(this::updateLayout);
        sentReload = false;
    }
}
