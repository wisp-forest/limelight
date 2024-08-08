package me.basiqueevangelist.limelight.impl.builtin;

import me.basiqueevangelist.limelight.api.action.InvokeResultEntryAction;
import me.basiqueevangelist.limelight.api.action.ResultEntryAction;
import me.basiqueevangelist.limelight.api.entry.ResultEntry;
import me.basiqueevangelist.limelight.api.entry.ResultGatherContext;
import me.basiqueevangelist.limelight.api.entry.ResultEntryGatherer;
import me.basiqueevangelist.limelight.api.module.LimelightModule;
import me.basiqueevangelist.limelight.impl.Limelight;
import me.basiqueevangelist.limelight.impl.resource.CalculatorResourceLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.objecthunter.exp4j.ExpressionBuilder;
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
    public void gatherEntries(ResultGatherContext ctx, Consumer<ResultEntry> entryConsumer) {
        var searchText = ctx.searchText();
        if (searchText.startsWith("=")) searchText = searchText.substring(1);

        double result;

        try {
            // construct expression builder
            var expression = new ExpressionBuilder(searchText)
                .variables(CalculatorResourceLoader.CONSTANTS.keySet())
                .build();

            expression.setVariables(CalculatorResourceLoader.CONSTANTS);

            // make sure the math maths correctly
            var valid = expression.validate(true);
            if (!valid.isValid()) return;

            result = expression.evaluate();

            // don't show the result if it's the same as the search text
//            if (new BigDecimal(searchText).equals(BigDecimal.valueOf(result))) return;

        } catch (Exception ignored) {
            return;
        }
        entryConsumer.accept(new CalculationResultEntry(BigDecimal.valueOf(result)));
    }

    @Override
    public @Nullable ResultEntryGatherer checkExclusiveGatherer(ResultGatherContext ctx) {
        if (!ctx.searchText().startsWith("=")) return null;

        return this;
    }

    private record CalculationResultEntry(BigDecimal result) implements ResultEntry, InvokeResultEntryAction {
        @Override
        public LimelightModule module() {
            return INSTANCE;
        }

        @Override
        public String entryId() {
            return ID.toString();
        }

        @Override
        public Text text() {
            var resultString = result.toPlainString();
            if (resultString.endsWith(".0")) resultString = resultString.substring(0, resultString.length() - 2);
            return Text.literal(resultString);
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
