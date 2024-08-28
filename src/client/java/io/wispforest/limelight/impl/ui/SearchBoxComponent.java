package io.wispforest.limelight.impl.ui;

import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.core.Sizing;
import org.lwjgl.glfw.GLFW;

public class SearchBoxComponent extends TextBoxComponent {
    private final Runnable enterHandler;

    public SearchBoxComponent(Sizing horizontalSizing, Runnable enterHandler) {
        super(horizontalSizing);
        this.enterHandler = enterHandler;
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_TAB)
            return false;
        else if (keyCode == GLFW.GLFW_KEY_ENTER) {
            enterHandler.run();
            return true;
        }

        return super.onKeyPress(keyCode, scanCode, modifiers);
    }

    @Override
    public void onFocusGained(FocusSource source) {
        super.onFocusGained(source);
        setSuggestion(null);
    }
}
