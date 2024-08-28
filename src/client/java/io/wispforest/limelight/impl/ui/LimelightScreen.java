package io.wispforest.limelight.impl.ui;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.util.Observable;
import io.wispforest.limelight.impl.Limelight;
import io.wispforest.limelight.impl.ResultGatherContextImpl;
import io.wispforest.limelight.impl.pond.TextFieldWidgetAccess;
import io.wispforest.limelight.api.util.CancellationTokenSource;
import io.wispforest.limelight.impl.util.ReactiveUtils;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class LimelightScreen extends BaseOwoScreen<FlowLayout> {
    private static String LAST_SEARCH_TEXT = "";

    private FlowLayout resultsContainer;
    ResultEntryComponent firstResult;
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

        searchBox.setMaxLength(Integer.MAX_VALUE);
        searchBox.setDrawsBackground(false);
        searchBox.setEditableColor(0xFF000000);
        searchBox.setPlaceholder(Text.translatable("text.limelight.search_hint"));
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

        searchBox.text(LAST_SEARCH_TEXT);
        searchBox.setSelectionStart(searchBox.getText().length());
        searchBox.setSelectionEnd(0);
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

        var ctx = new ResultGatherContextImpl(searchText, resultsTokenSource.token());
        resultsContainer.child(new ResultsContainerComponent(this, ctx));
    }

    @Override
    public boolean shouldPause() {
        return Limelight.CONFIG.pauseGameWhileInScreen();
    }

    @Override
    public void removed() {
        super.removed();

        LAST_SEARCH_TEXT = searchBox.getText();

        if (resultsTokenSource != null) resultsTokenSource.cancel();
    }
}
