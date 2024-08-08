package me.basiqueevangelist.limelight.impl.builtin;

import me.basiqueevangelist.limelight.api.entry.ResultGatherContext;
import me.basiqueevangelist.limelight.api.module.LimelightModule;
import me.basiqueevangelist.limelight.api.entry.ResultEntry;
import me.basiqueevangelist.limelight.api.action.InvokeAction;
import me.basiqueevangelist.limelight.api.action.ResultAction;
import me.basiqueevangelist.limelight.impl.Limelight;
import me.basiqueevangelist.limelight.mixin.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class KeyBindingsModule implements LimelightModule {
    public static final Identifier ID = Limelight.id("key_bindings");
    public static final KeyBindingsModule INSTANCE = new KeyBindingsModule();

    private KeyBindingsModule() { }

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

    private record KeyBindingResult(KeyBinding binding) implements ResultEntry, InvokeAction {
        @Override
        public LimelightModule module() {
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
        public ResultAction action() {
            return this;
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
