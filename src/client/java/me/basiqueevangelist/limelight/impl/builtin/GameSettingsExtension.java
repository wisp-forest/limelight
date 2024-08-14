package me.basiqueevangelist.limelight.impl.builtin;

import me.basiqueevangelist.limelight.api.action.ResultAction;
import me.basiqueevangelist.limelight.api.action.ToggleAction;
import me.basiqueevangelist.limelight.api.entry.ResultEntry;
import me.basiqueevangelist.limelight.api.entry.ResultGatherContext;
import me.basiqueevangelist.limelight.api.extension.LimelightExtension;
import me.basiqueevangelist.limelight.impl.Limelight;
import me.basiqueevangelist.limelight.mixin.SimpleOptionAccessor;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class GameSettingsExtension implements LimelightExtension {
    public static final Identifier ID = Limelight.id("game_settings");
    public static final GameSettingsExtension INSTANCE = new GameSettingsExtension();

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public void gatherEntries(ResultGatherContext ctx, Consumer<ResultEntry> entryConsumer) {
        List<Pair<String, SimpleOption<Boolean>>> booleanOptions = new ArrayList<>();

        ctx.client().options.accept(new GameOptions.Visitor() {
            @Override
            public int visitInt(String key, int current) {
                return current;
            }

            @Override
            public boolean visitBoolean(String key, boolean current) {
                return current;
            }

            @Override
            public String visitString(String key, String current) {
                return current;
            }

            @Override
            public float visitFloat(String key, float current) {
                return current;
            }

            @Override
            public <T> T visitObject(String key, T current, Function<String, T> decoder, Function<T, String> encoder) {
                return current;
            }

            @SuppressWarnings("unchecked")
            @Override
            public <T> void accept(String key, SimpleOption<T> option) {
                if (!(option.getValue() instanceof Boolean)) return;

                booleanOptions.add(new Pair<>(key, (SimpleOption<Boolean>) option));
            }
        });

        for (var option : booleanOptions) {
            var entry = new BoolOptionEntry(option.getLeft(), option.getRight());
            if (!ctx.matches(entry.text().getString())) continue;
            entryConsumer.accept(entry);
        }
    }

    private record BoolOptionEntry(String key, SimpleOption<Boolean> option) implements ResultEntry, ToggleAction {
        @Override
        public boolean getValue() {
            return option.getValue();
        }

        @Override
        public void setValue(boolean value) {
            option.setValue(value);
        }

        @Override
        public LimelightExtension extension() {
            return INSTANCE;
        }

        @Override
        public String entryId() {
            return "limelight:game_settings/" + key;
        }

        @Override
        public Text text() {
            return ((SimpleOptionAccessor)(Object) option).getText();
        }

        @Override
        public ResultAction action() {
            return this;
        }
    }
}
