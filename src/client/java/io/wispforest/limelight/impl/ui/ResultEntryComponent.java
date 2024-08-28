package io.wispforest.limelight.impl.ui;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.SmallCheckboxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.limelight.api.entry.SetSearchTextEntry;
import io.wispforest.limelight.api.entry.ToggleResultEntry;
import io.wispforest.limelight.api.entry.ResultEntry;
import io.wispforest.limelight.api.entry.InvokeResultEntry;
import io.wispforest.limelight.impl.Limelight;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class ResultEntryComponent extends FlowLayout {
    private final LimelightScreen screen;
    private final ResultEntry entry;
    private final @Nullable SmallCheckboxComponent toggleBox;

    public ResultEntryComponent(LimelightScreen screen, ResultEntry entry) {
        super(Sizing.fill(), Sizing.content(), Algorithm.HORIZONTAL);
        this.screen = screen;
        this.entry = entry;

        padding(Insets.both(2, 4));

        var extLabel = Components.label(Text.empty()
            .append(entry.extension().name())
            .append(" ")
            .formatted(Formatting.DARK_GRAY));

        extLabel.tooltip(entry.extension().tooltip());

        child(extLabel);

        child(Components.label(Text.empty()
            .append(entry.text())
            .formatted(Formatting.BLACK)));

        if (entry instanceof ToggleResultEntry toggle) {
            this.toggleBox = Components.smallCheckbox(null);

            this.toggleBox.checked(toggle.getValue());
            this.toggleBox.onChanged().subscribe(toggle::setValue);

            child(Components.spacer().verticalSizing(Sizing.fixed(0)));
            child(this.toggleBox);
        } else {
            this.toggleBox = null;
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
                // hey guys did you know I love ConcurrentModificationExceptions
                MinecraftClient.getInstance().send(() -> {
                    screen.searchBox.setText(setSearchText.newSearchText());
                    screen.searchBox.root().focusHandler().focus(screen.searchBox, FocusSource.KEYBOARD_CYCLE);
                });
            }
            case ToggleResultEntry ignored -> {
                toggleBox.checked(!toggleBox.checked());
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
        } else {
            root().focusHandler().focus(screen.searchBox, FocusSource.MOUSE_CLICK);
            screen.searchBox.onKeyPress(keyCode, scanCode, modifiers);
            return true;
        }

        return super.onKeyPress(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean onCharTyped(char chr, int modifiers) {
        root().focusHandler().focus(screen.searchBox, FocusSource.MOUSE_CLICK);
        screen.searchBox.onCharTyped(chr, modifiers);

        return true;
    }

    @Override
    public void onFocusGained(FocusSource source) {
        super.onFocusGained(source);
        surface(Surface.outline(0xFFFFFFFF));

        applySuggestion();
    }

    @Override
    public void onFocusLost() {
        super.onFocusLost();
        surface(Surface.BLANK);
        screen.searchBox.setSuggestion(null);
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }
}
