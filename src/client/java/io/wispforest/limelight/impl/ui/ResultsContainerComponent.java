package io.wispforest.limelight.impl.ui;

import io.wispforest.limelight.api.entry.ExpandableResultEntry;
import io.wispforest.owo.ui.container.Containers;
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
    private final Map<ExpandableResultEntry,Boolean> expanded = new HashMap<>();

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
            int index = children.indexOf(component)+1;
            if (expanded.getOrDefault(resultEntry, false)) {
                expanded.put(resultEntry, false);
                removeChild(children.get(index));
            }
            else {
                expanded.put(resultEntry, true);
                var col = Containers.verticalFlow(Sizing.fill(), Sizing.content());
                for (ResultEntry childEntry : resultEntry.children())
                    col.child(new ResultEntryComponent(screen, childEntry, true));
                child(index, col);
                component.root().focusHandler().focus(component, FocusSource.KEYBOARD_CYCLE);
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
                var result = new ResultEntryComponent(screen, entry, false);

                if (screen.firstResult == null) screen.firstResult = result;

                child(result);

            }
        });

        if (parent != null) queue(this::updateLayout);
        sentReload = false;
        screen.updateResultCounter(this);
    }
}
