package io.wispforest.limelight.impl.ui;

import io.wispforest.limelight.impl.config.LimelightTheme;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.RotationAxis;

class ExpandIndicatorComponent extends LabelComponent {

    public final boolean isExpanded;

    public ExpandIndicatorComponent(boolean isExpanded) {
        super(Text.literal(">").withColor(LimelightTheme.current().sourceExtensionColor()).formatted(Formatting.BOLD));
        this.cursorStyle(CursorStyle.HAND);
        this.isExpanded = isExpanded;
    }


    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        var matrices = context.getMatrices();

        matrices.push();
        matrices.translate(this.x + this.width / 2f - 1, this.y + this.height / 2f - 1, 0);
        if (isExpanded)
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));
        matrices.translate(-(this.x + this.width / 2f - 1), -(this.y + this.height / 2f - 1), 0);

        super.draw(context, mouseX, mouseY, partialTicks, delta);
        matrices.pop();
    }
}
