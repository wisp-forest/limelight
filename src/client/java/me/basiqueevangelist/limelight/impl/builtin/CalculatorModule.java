package me.basiqueevangelist.limelight.impl.builtin;

import me.basiqueevangelist.limelight.api.action.InvokeResultEntryAction;
import me.basiqueevangelist.limelight.api.action.ResultEntryAction;
import me.basiqueevangelist.limelight.api.entry.ResultEntry;
import me.basiqueevangelist.limelight.api.entry.ResultEntryGatherer;
import me.basiqueevangelist.limelight.api.module.LimelightModule;
import me.basiqueevangelist.limelight.impl.Limelight;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class CalculatorModule implements LimelightModule {
    public static final Identifier ID = Limelight.id("calculator");
    public static final CalculatorModule INSTANCE = new CalculatorModule();

    private CalculatorModule() { }

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public void gatherEntries(String searchText, Consumer<ResultEntry> entryConsumer) {
        if (searchText.startsWith("=")) searchText = searchText.substring(1);

        // Do calculations?
        entryConsumer.accept(new CalculationResultEntry(BigDecimal.valueOf(21)));
    }

    @Override
    public @Nullable ResultEntryGatherer checkExclusiveGatherer(String searchText) {
        if (!searchText.startsWith("=")) return null;

        return this;
    }

    private record CalculationResultEntry(BigDecimal result) implements ResultEntry, InvokeResultEntryAction {
        @Override
        public LimelightModule module() {
            return INSTANCE;
        }

        @Override
        public Identifier entryId() {
            return ID;
        }

        @Override
        public Text text() {
            return Text.literal(result.toPlainString());
        }

        @Override
        public ResultEntryAction action() {
            return this;
        }

        @Override
        public void run() {
            MinecraftClient.getInstance().keyboard.setClipboard(result.toPlainString());
        }
    }
}
