package me.basiqueevangelist.limelight.impl.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.wispforest.endec.Endec;
import io.wispforest.endec.format.gson.GsonDeserializer;
import io.wispforest.endec.impl.StructEndecBuilder;
import me.basiqueevangelist.limelight.impl.Limelight;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CalculatorResourceLoader extends SinglePreparationResourceReloader<CalculatorResourceLoader.Payload> implements IdentifiableResourceReloadListener {
    public static final Identifier ID = Limelight.id("calculator_resources");
    public static final IdentifiableResourceReloadListener INSTANCE = new CalculatorResourceLoader();

    private static final ResourceFinder FINDER = ResourceFinder.json("limelight/calculator");
    private static final Gson GSON = new GsonBuilder().setLenient().disableHtmlEscaping().create();
    private static final Logger LOGGER = LoggerFactory.getLogger("Limelight/CalculatorResourceLoader");

    public static Map<String, Double> CONSTANTS = Map.of();

    private CalculatorResourceLoader() { }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    protected Payload prepare(ResourceManager manager, Profiler profiler) {
        Map<String, Double> constants = new HashMap<>();

        for (var entry : FINDER.findResources(manager).entrySet()) {
            FileData data;

            try (var reader = entry.getValue().getReader()) {
                JsonElement element = JsonHelper.deserialize(GSON, reader, JsonElement.class);
                data = FileData.ENDEC.decodeFully(GsonDeserializer::of, element);
            } catch (Exception e) {
                LOGGER.warn("Could not load calculator resource '{}'", entry.getKey(), e);
                continue;
            }

            constants.putAll(data.constants());
        }

        return new Payload(constants);
    }

    @Override
    protected void apply(Payload prepared, ResourceManager manager, Profiler profiler) {
        CONSTANTS = prepared.constants();
    }

    public record Payload(Map<String, Double> constants) { }

    public record FileData(Map<String, Double> constants) {
        public static final Endec<FileData> ENDEC = StructEndecBuilder.of(
            Endec.map(Function.identity(), Function.identity(), Endec.DOUBLE)
                .optionalFieldOf("constants", FileData::constants, Map.of()),
            FileData::new
        );
    }
}
