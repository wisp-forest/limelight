package me.basiqueevangelist.limelight.impl;

import io.wispforest.owo.util.Observable;
import me.basiqueevangelist.limelight.api.LimelightEntrypoint;
import me.basiqueevangelist.limelight.api.extension.LimelightExtension;
import me.basiqueevangelist.limelight.impl.util.ReactiveUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ExtensionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("Limelight/ExtensionManager");
    private static final Map<Identifier, LimelightExtension> EXTENSIONS_BY_ID = new HashMap<>();
    private static List<LimelightExtension> EXTENSIONS;
    private static Observable<List<LimelightExtension>> ENABLED_EXTENSIONS;

    private ExtensionManager() {

    }

    public static void init() {
        var entrypoints = FabricLoader.getInstance().getEntrypointContainers(LimelightEntrypoint.KEY, LimelightEntrypoint.class);
        Map<Identifier, LimelightExtension> extensions = new HashMap<>();

        for (var entry : entrypoints) {
            String modId = entry.getProvider().getMetadata().getId();

            try {
                var api = entry.getEntrypoint();
                api.registerExtensions(extension -> {
                    var id = extension.id();

                    if (extensions.containsKey(id)) {
                        LOGGER.warn("{} tried to register an extension under {}, which was already taken", modId, id);
                        return;
                    }

                    extensions.put(id, extension);

                    Limelight.CONFIG.enabledExtensions().putIfAbsent(id, true);
                });
            } catch (Throwable t) {
                LOGGER.error("{}'s LimelightEntrypoint handler threw an error.", modId, t);
            }
        }

        EXTENSIONS_BY_ID.clear();
        EXTENSIONS_BY_ID.putAll(extensions);

        EXTENSIONS = EXTENSIONS_BY_ID.values().stream().toList();

        LOGGER.info("Loaded {} Limelight extensions.", EXTENSIONS_BY_ID.size());

        // glisco makes a dollar, I make a dime, that's why I write shit code on company time
        ENABLED_EXTENSIONS = me.basiqueevangelist.limelight.impl.util.ReactiveUtils.map(me.basiqueevangelist.limelight.impl.util.ReactiveUtils.from(me.basiqueevangelist.limelight.impl.Limelight.CONFIG.<java.util.Map<net.minecraft.util.Identifier,java.lang.Boolean>>optionForKey(me.basiqueevangelist.limelight.impl.Limelight.CONFIG.keys.enabledExtensions)), e -> {
            java.util.List<me.basiqueevangelist.limelight.api.extension.LimelightExtension> enabled = new java.util.ArrayList<>(EXTENSIONS);
            enabled.removeIf(x -> !e.getOrDefault(x.id(), true));
            return enabled;
        });
    }

    public static List<LimelightExtension> allExtensions() {
        return EXTENSIONS;
    }

    public static List<LimelightExtension> enabledExtensions() {
        return ENABLED_EXTENSIONS.get();
    }
}
