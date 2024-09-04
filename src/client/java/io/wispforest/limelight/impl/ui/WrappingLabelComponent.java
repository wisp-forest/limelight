package io.wispforest.limelight.impl.ui;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.core.Size;
import net.minecraft.text.Text;

public class WrappingLabelComponent extends LabelComponent {
    public WrappingLabelComponent(Text text) {
        super(text);
    }

    @Override
    public void inflate(Size space) {
        this.maxWidth = space.width();
        super.inflate(space);
    }
}
