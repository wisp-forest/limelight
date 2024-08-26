package me.basiqueevangelist.limelight.impl.builtin.wiki;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.wispforest.endec.format.gson.GsonDeserializer;
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

import java.util.*;

public class WikiLoader extends SinglePreparationResourceReloader<Map<Identifier, WikiDescription<?>>> implements IdentifiableResourceReloadListener {
    public static final Identifier ID = Limelight.id("wiki");
    public static final IdentifiableResourceReloadListener INSTANCE = new WikiLoader();

    private static final ResourceFinder FINDER = ResourceFinder.json("limelight/wiki");
    private static final Gson GSON = new GsonBuilder().setLenient().disableHtmlEscaping().create();
    private static final Logger LOGGER = LoggerFactory.getLogger("Limelight/WikiLoader");

    public static Map<Identifier, WikiDescription<?>> WIKIS = Map.of();

    private WikiLoader() { }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    protected Map<Identifier, WikiDescription<?>> prepare(ResourceManager manager, Profiler profiler) {
        Map<Identifier, WikiDescription<?>> descs = new HashMap<>();

        for (var entry : FINDER.findResources(manager).entrySet()) {
            WikiDescription<?> data;

            try (var reader = entry.getValue().getReader()) {
                JsonElement element = JsonHelper.deserialize(GSON, reader, JsonElement.class);
                data = WikiDescription.ENDEC.decodeFully(GsonDeserializer::of, element);
            } catch (Exception e) {
                LOGGER.warn("Could not load wiki '{}'", entry.getKey(), e);
                continue;
            }

            descs.put(FINDER.toResourceId(entry.getKey()), data);
        }

        return descs;
    }

    @Override
    protected void apply(Map<Identifier, WikiDescription<?>> prepared, ResourceManager manager, Profiler profiler) {
        WIKIS = prepared;
    }
}
