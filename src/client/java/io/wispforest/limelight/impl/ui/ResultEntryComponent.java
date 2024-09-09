package io.wispforest.limelight.impl.ui;

import io.wispforest.limelight.api.entry.*;
import io.wispforest.limelight.impl.config.LimelightTheme;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.SmallCheckboxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.limelight.impl.Limelight;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class ResultEntryComponent extends FlowLayout {
    private final LimelightScreen screen;
    private final ResultEntry entry;
    private final @Nullable SmallCheckboxComponent toggleBox;
    private final boolean expandedChild;

    public ResultEntryComponent(LimelightScreen screen, ResultEntry entry, boolean expandedChild, boolean expanded) {
        super(Sizing.fill(), Sizing.content(), Algorithm.HORIZONTAL);
        this.screen = screen;
        this.entry = entry;
        this.expandedChild = expandedChild;

        LimelightTheme theme = LimelightTheme.current();

        padding(Insets.both(2, 4));
        if (expandedChild)
            surface(Surface.flat(theme.expandedResultChildEntryBackgroundColor()));

        MutableText labelBuilder = Text.empty();

        MutableText tooltipText = Text.empty();

        boolean first = true;
        for (var line : entry.extension().tooltip()) {
            if (!first) tooltipText.append("\n");
            first = false;

            tooltipText.append(line);
        }

        labelBuilder.append(
            Text.empty()
                .append(entry.extension().name())
                .styled(x -> x.withColor(expandedChild ? theme.expandedResultChildEntrySourceExtensionColor() : theme.sourceExtensionColor()))
                .styled(x -> x.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltipText)))
        );

        labelBuilder.append(" ");

        labelBuilder.append(Text.empty()
            .append(entry.text())
            .styled(x -> x.withColor(theme.resultEntryTextColor())));

        child(new WrappingLabelComponent(labelBuilder));

        if (entry instanceof ToggleResultEntry toggle) {
            this.toggleBox = Components.smallCheckbox(null);

            this.toggleBox.checked(toggle.getValue());
            this.toggleBox.onChanged().subscribe(toggle::setValue);

            child(Components.spacer().verticalSizing(Sizing.fixed(0)));
            child(this.toggleBox);
        } else {
            this.toggleBox = null;
        }

        if (entry instanceof ExpandableResultEntry) {
            child(Components.spacer().verticalSizing(Sizing.fixed(0)).horizontalSizing(Sizing.expand(100)));
            child(new ExpandIndicatorComponent(expanded));
        }
    }

    public void run() {
        Limelight.ENTRY_USES.bump(entry.entryId());

        switch (entry) {
            case InvokeResultEntry invoke -> {
                if (invoke.closesScreen()) screen.close();
                invoke.run();
            }
            case SetSearchTextEntry setSearchText -> {
                // hey guys did you know I love ConcurrentModificationExceptions; same
                MinecraftClient.getInstance().send(() -> {
                    screen.searchBox.setText(setSearchText.newSearchText());
                    screen.searchBox.root().focusHandler().focus(screen.searchBox, FocusSource.KEYBOARD_CYCLE);
                });
            }
            case ToggleResultEntry ignored -> {
                toggleBox.checked(!toggleBox.checked());
            }
            case ExpandableResultEntry expanded -> {
                if (parent != null)
                    ((ResultsContainerComponent)parent).toggleExpanded(expanded.entryId());
            }
        }
    }

    public void applySuggestion() {
        if (entry instanceof SetSearchTextEntry setSearchText) {
            String newText = setSearchText.newSearchText();
            String suffix = newText.startsWith(screen.searchBox.getText()) ? newText.substring(screen.searchBox.getText().length()) : null;
            screen.searchBox.setSuggestion(suffix);
        } else {
            screen.searchBox.setSuggestion(null);
        }
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            run();
        }

        return super.onMouseDown(mouseX, mouseY, button);
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            run();
        } else if (root() != null){
            root().focusHandler().focus(screen.searchBox, FocusSource.MOUSE_CLICK);
            screen.searchBox.onKeyPress(keyCode, scanCode, modifiers);
            return true;
        }

        return super.onKeyPress(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onCharTyped(char chr, int modifiers) {
        if (root() != null) {
            root().focusHandler().focus(screen.searchBox, FocusSource.MOUSE_CLICK);
            screen.searchBox.onCharTyped(chr, modifiers);
            return true;
        }
        return false;
    }

    @Override
    public void onFocusGained(FocusSource source) {
        super.onFocusGained(source);
        Surface s = Surface.outline(LimelightTheme.current().focusOutlineColor());
        if (expandedChild)
            s = s.and(Surface.flat(LimelightTheme.current().expandedResultChildEntryBackgroundColor()));
        surface(s);

        applySuggestion();
    }

    @Override
    public void onFocusLost() {
        super.onFocusLost();
        if (expandedChild)
            surface(Surface.flat(LimelightTheme.current().expandedResultChildEntryBackgroundColor()));
        else
            surface(Surface.BLANK);
        screen.searchBox.setSuggestion(null);
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }
}
