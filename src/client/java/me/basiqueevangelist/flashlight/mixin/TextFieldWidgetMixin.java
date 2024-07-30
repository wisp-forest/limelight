package me.basiqueevangelist.flashlight.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.basiqueevangelist.flashlight.impl.pond.TextFieldWidgetAccess;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TextFieldWidget.class)
public class TextFieldWidgetMixin implements TextFieldWidgetAccess {
    @Unique private boolean flashlight$drawShadow = true;

    @Override
    public void flashlight$setDrawShadow(boolean drawShadow) {
        flashlight$drawShadow = drawShadow;
    }

    @WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)I"))
    private int noShadow(DrawContext instance, TextRenderer textRenderer, String text, int x, int y, int color, Operation<Integer> original) {
        if (flashlight$drawShadow) {
            return original.call(instance, textRenderer, text, x, y, color);
        } else {
            return instance.drawText(textRenderer, text, x, y, color, false);
        }
    }

    @WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)I"))
    private int noShadow(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int color, Operation<Integer> original) {
        if (flashlight$drawShadow) {
            return original.call(instance, textRenderer, text, x, y, color);
        } else {
            return instance.drawText(textRenderer, text, x, y, color, false);
        }
    }

    @WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;III)I"))
    private int noShadow(DrawContext instance, TextRenderer textRenderer, OrderedText text, int x, int y, int color, Operation<Integer> original) {
        if (flashlight$drawShadow) {
            return original.call(instance, textRenderer, text, x, y, color);
        } else {
            return instance.drawText(textRenderer, text, x, y, color, false);
        }
    }
}
