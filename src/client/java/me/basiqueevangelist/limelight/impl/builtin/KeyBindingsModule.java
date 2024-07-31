package me.basiqueevangelist.limelight.impl.builtin;

import me.basiqueevangelist.limelight.api.module.LimelightModule;
import me.basiqueevangelist.limelight.api.entry.ResultEntry;
import me.basiqueevangelist.limelight.api.action.InvokeResultEntryAction;
import me.basiqueevangelist.limelight.api.action.ResultEntryAction;
import me.basiqueevangelist.limelight.impl.Limelight;
import me.basiqueevangelist.limelight.mixin.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
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
    public void gatherEntries(String searchText, Consumer<ResultEntry> entryConsumer) {
        for (var key : MinecraftClient.getInstance().options.allKeys) {
            var entry = new KeyBindingResult(key);
            if (!StringUtils.containsIgnoreCase(entry.text().getString(), searchText)) continue;
            entryConsumer.accept(entry);
        }
    }

    private record KeyBindingResult(KeyBinding binding) implements ResultEntry, InvokeResultEntryAction {
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
        public ResultEntryAction action() {
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
