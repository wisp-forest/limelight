package io.wispforest.limelight.impl.ui;

import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.util.FocusHandler;
import org.jetbrains.annotations.Nullable;

public class SearchFocusHandler extends FocusHandler {
    public SearchFocusHandler(ParentComponent root) {
        super(root);
    }

    @Override
    public void focus(@Nullable Component component, Component.FocusSource source) {
        super.focus(component, source);

        if (component == null) return;

        ParentComponent current = component.parent();
        while (current != null) {
            if (current instanceof ScrollContainer<?> scroll) {
                scroll.scrollTo(component);
            }
            
            current = current.parent();
        }
    }
}
