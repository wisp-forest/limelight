package io.wispforest.limelight.impl.ui;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.core.CursorStyle;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.util.Delta;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.RotationAxis;

class ExpandIndicatorComponent extends LabelComponent {

    protected float rotation = 0;
    protected float targetRotation = 0;

    public ExpandIndicatorComponent(int color) {
        super(Text.literal(">").withColor(color).formatted(Formatting.BOLD));
        this.cursorStyle(CursorStyle.HAND);
    }

    public void toggle() {
        targetRotation = targetRotation == 0 ? 90 : 0;
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
        this.rotation += Delta.compute(this.rotation, this.targetRotation, delta * .65);
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        var matrices = context.getMatrices();

        matrices.push();
        matrices.translate(this.x + this.width / 2f - 1, this.y + this.height / 2f - 1, 0);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(this.rotation));
        matrices.translate(-(this.x + this.width / 2f - 1), -(this.y + this.height / 2f - 1), 0);

        super.draw(context, mouseX, mouseY, partialTicks, delta);
        matrices.pop();
    }
}