package me.basiqueevangelist.flashlight.impl.module;

import me.basiqueevangelist.flashlight.api.ResultEntry;
import me.basiqueevangelist.flashlight.api.ResultGatherEvents;
import me.basiqueevangelist.flashlight.api.action.InvokeResultEntryAction;
import me.basiqueevangelist.flashlight.api.action.ResultEntryAction;
import me.basiqueevangelist.flashlight.mixin.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

public class KeyBindingsModule {
    public static final Text CATEGORY = Text.translatable("flashlightCategory.flashlight.keybindings");

    public static void init() {
        ResultGatherEvents.GATHER.register((searchText, entryConsumer) -> {
            for (var key : MinecraftClient.getInstance().options.allKeys) {
                var entry = new KeyBindingResult(key);
                if (!StringUtils.containsIgnoreCase(entry.text().getString(), searchText)) continue;
                entryConsumer.accept(entry);
            }
        });
    }

    private record KeyBindingResult(KeyBinding binding) implements ResultEntry, InvokeResultEntryAction {
        @Override
        public Text categoryName() {
            return CATEGORY;
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
