package io.wispforest.limelight.impl.ui;

import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Sizing;
import org.lwjgl.glfw.GLFW;

public class SearchRootFlowLayout extends FlowLayout {
    public SearchRootFlowLayout(Sizing horizontalSizing, Sizing verticalSizing, Algorithm algorithm) {
        super(horizontalSizing, verticalSizing, algorithm);
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (this.focusHandler == null) return false;

        if (keyCode == GLFW.GLFW_KEY_TAB) {
            this.focusHandler.cycle((modifiers & GLFW.GLFW_MOD_SHIFT) == 0);
        } else if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_UP) {
            this.focusHandler.moveFocus(keyCode);
        } else if (this.focusHandler.focused() != null) {
            return this.focusHandler.focused().onKeyPress(keyCode, scanCode, modifiers);
        }

        return this.keyPressEvents.sink().onKeyPress(keyCode, scanCode, modifiers);
    }

    @Override
    public void mount(ParentComponent parent, int x, int y) {
        super.mount(parent, x, y);

        if (parent == null) {
            this.focusHandler = new SearchFocusHandler(this);
        }
    }
}
