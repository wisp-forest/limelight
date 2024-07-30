package me.basiqueevangelist.flashlight.impl.ui;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import me.basiqueevangelist.flashlight.api.ResultEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class ResultEntryComponent extends FlowLayout {
    private final FlashlightScreen screen;
    private final ResultEntry entry;

    public ResultEntryComponent(FlashlightScreen screen, ResultEntry entry) {
        super(Sizing.fill(), Sizing.content(), Algorithm.HORIZONTAL);
        this.screen = screen;
        this.entry = entry;

        padding(Insets.vertical(4));

        child(Components.label(Text.empty()
            .append(entry.categoryName())
            .append(" ")
            .formatted(Formatting.DARK_GRAY)));

        child(Components.label(Text.empty()
            .append(entry.text())
            .formatted(Formatting.BLACK)));
    }

    public void run() {
        screen.close();
        entry.run();
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
        }

        return super.onKeyPress(keyCode, scanCode, modifiers);
    }

    @Override
    public void onFocusGained(FocusSource source) {
        super.onFocusGained(source);
        surface(Surface.outline(0xFFFFFFFF));
    }

    @Override
    public void onFocusLost() {
        super.onFocusLost();
        surface(Surface.BLANK);
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }
}
