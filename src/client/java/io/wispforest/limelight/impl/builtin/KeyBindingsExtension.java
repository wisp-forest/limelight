package io.wispforest.limelight.impl.builtin;

import io.wispforest.limelight.api.entry.ResultGatherContext;
import io.wispforest.limelight.api.extension.LimelightExtension;
import io.wispforest.limelight.api.entry.ResultEntry;
import io.wispforest.limelight.api.entry.InvokeResultEntry;
import io.wispforest.limelight.impl.Limelight;
import io.wispforest.limelight.mixin.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class KeyBindingsExtension implements LimelightExtension {
    public static final Identifier ID = Limelight.id("key_bindings");
    public static final KeyBindingsExtension INSTANCE = new KeyBindingsExtension();

    private KeyBindingsExtension() { }

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public void gatherEntries(ResultGatherContext ctx, Consumer<ResultEntry> entryConsumer) {
        for (var key : ctx.client().options.allKeys) {
            var entry = new KeyBindingResult(key);
            if (!ctx.matches(entry.text().getString())) continue;
            entryConsumer.accept(entry);
        }
    }

    private record KeyBindingResult(KeyBinding binding) implements InvokeResultEntry {
        @Override
        public LimelightExtension extension() {
            return INSTANCE;
        }

        @Override
        public String entryId() {
            return Limelight.MOD_ID + ":key_bindings/" + binding.getTranslationKey();
        }

        @Override
        public Text text() {
            return Text.empty()
                .append(Text.translatable(this.binding.getCategory()))
                .append(" > ")
                .append(Text.translatable(this.binding.getTranslationKey()));
        }

        @Override
        public void run() {
            var access = (KeyBindingAccessor) binding;
            access.setTimesPressed(access.getTimesPressed() + 1);

            // makes StickyKeyBinding work.
            binding.setPressed(true);
            binding.setPressed(false);

            MinecraftClient.getInstance().attackCooldown = 0;
        }
    }
}
