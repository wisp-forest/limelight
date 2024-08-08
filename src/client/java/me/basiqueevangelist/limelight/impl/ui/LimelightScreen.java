package me.basiqueevangelist.limelight.impl.ui;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.util.Observable;
import me.basiqueevangelist.limelight.impl.Limelight;
import me.basiqueevangelist.limelight.impl.ResultGatherContextImpl;
import me.basiqueevangelist.limelight.impl.ResultGatherer;
import me.basiqueevangelist.limelight.impl.pond.TextFieldWidgetAccess;
import me.basiqueevangelist.limelight.impl.util.CancellationTokenSource;
import me.basiqueevangelist.limelight.impl.util.ReactiveUtils;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class LimelightScreen extends BaseOwoScreen<FlowLayout> {
    private FlowLayout resultsContainer;
    private ResultEntryComponent firstResult;
    private CancellationTokenSource resultsTokenSource = null;
    TextBoxComponent searchBox;

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, (h, v) -> new SearchRootFlowLayout(h, v, FlowLayout.Algorithm.VERTICAL));
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        FlowLayout sizeLimitFlow = Containers.verticalFlow(Sizing.fill(40), Sizing.fill(50));

        rootComponent
            .child(sizeLimitFlow)
            .alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);

        FlowLayout popupComponent = Containers.verticalFlow(Sizing.fill(), Sizing.content());
        sizeLimitFlow.child(popupComponent);

        popupComponent
            .surface(Surface.flat(0xAAFBFAF5))
            .padding(Insets.of(5));

        FlowLayout searchRow = Containers.horizontalFlow(Sizing.fill(), Sizing.content());
        popupComponent.child(searchRow);
        searchRow.verticalAlignment(VerticalAlignment.CENTER);

        searchRow
            // TODO: actually use our own texture instead of piggy-backing off owo-lib
            .child(Components.texture(Identifier.of("owo:textures/gui/config_search.png"), 0, 0, 16, 16, 16, 16));

        this.searchBox = new SearchBoxComponent(Sizing.fill(), () -> {
            if (firstResult != null) firstResult.run();
        });

        searchBox.setDrawsBackground(false);
        searchBox.setEditableColor(0xFF000000);
        //noinspection DataFlowIssue
        ((TextFieldWidgetAccess) searchBox).limelight$setDrawShadow(false);

        searchRow.child(searchBox);

        Observable<String> contents = Observable.of(searchBox.getText());
        searchBox.onChanged().subscribe(contents::set);

        ReactiveUtils.throttle(contents, TimeUnit.MILLISECONDS.toNanos(100), client)
            .observe(this::rebuildContentsWith);

        this.resultsContainer = Containers.verticalFlow(Sizing.fill(), Sizing.content());
        popupComponent.child(resultsContainer);

        client.send(() -> {
            rootComponent.focusHandler().focus(searchBox, Component.FocusSource.MOUSE_CLICK);
        });
    }

    private void rebuildContentsWith(String searchText) {
        if (resultsTokenSource != null) resultsTokenSource.cancel();
        resultsContainer.clearChildren();
        firstResult = null;
        resultsTokenSource = new CancellationTokenSource();

        if (searchText.isEmpty()) {
            return;
        }

        resultsContainer.child(Components.box(Sizing.fill(), Sizing.fixed(2))
            .margins(Insets.vertical(3)));

        var entries = ResultGatherer.gatherResults(new ResultGatherContextImpl(searchText, resultsTokenSource.token()));

        for (var entry : entries) {
            var result = new ResultEntryComponent(this, entry);

            if (firstResult == null) firstResult = result;

            resultsContainer.child(result);
        }
    }

    @Override
    public boolean shouldPause() {
        return Limelight.CONFIG.get().pauseGameWhileInScreen;
    }
}
