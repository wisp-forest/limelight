package io.wispforest.limelight.impl.config;

import io.wispforest.owo.config.ui.component.ConfigToggleButton;
import io.wispforest.owo.util.Observable;
import net.minecraft.text.Text;

import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class ExtensionConfigButton extends ConfigToggleButton {
    protected final Observable<Boolean> listeners = Observable.of(this.enabled);

    public ExtensionConfigButton onChanged(Consumer<Boolean> listener) {
        this.listeners.observe(listener);
        return this;
    }

    @Override
    protected void updateMessage() {
        if (listeners != null) listeners.set(enabled);

        this.setMessage(enabled
            ? Text.translatable("text.config.limelight.extension_config.enabled")
            : Text.translatable("text.config.limelight.extension_config.disabled")
        );
    }
}