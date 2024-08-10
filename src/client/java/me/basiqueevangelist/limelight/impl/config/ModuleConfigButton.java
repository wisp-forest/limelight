package me.basiqueevangelist.limelight.impl.config;

import io.wispforest.owo.config.ui.component.ConfigToggleButton;
import io.wispforest.owo.util.Observable;
import net.minecraft.text.Text;

import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class ModuleConfigButton extends ConfigToggleButton {
    protected final Observable<Boolean> listeners = Observable.of(this.enabled);

    public ModuleConfigButton onChanged(Consumer<Boolean> listener) {
        this.listeners.observe(listener);
        return this;
    }

    @Override
    protected void updateMessage() {
        if (listeners != null) listeners.set(enabled);

        this.setMessage(enabled
            ? Text.translatable("text.config.limelight.module_config.enabled")
            : Text.translatable("text.config.limelight.module_config.disabled")
        );
    }
}