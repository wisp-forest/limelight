package me.basiqueevangelist.limelight.impl;

import me.basiqueevangelist.limelight.api.LimelightEntrypoint;
import me.basiqueevangelist.limelight.api.module.LimelightModule;
import me.basiqueevangelist.limelight.impl.config.LimelightConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ModuleManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("Limelight/ModuleManager");
    private static final Map<Identifier, LimelightModule> MODULES_BY_ID = new HashMap<>();
    private static List<LimelightModule> MODULES;

    private ModuleManager() {

    }

    public static void init() {
        var entrypoints = FabricLoader.getInstance().getEntrypointContainers(LimelightEntrypoint.KEY, LimelightEntrypoint.class);
        Map<Identifier, LimelightModule> modules = new HashMap<>();

        for (var entry : entrypoints) {
            String modId = entry.getProvider().getMetadata().getId();

            try {
                var api = entry.getEntrypoint();
                api.registerModules(module -> {
                    var id = module.id();

                    if (modules.containsKey(id)) {
                        LOGGER.warn("{} tried to register a module under {}, which was already taken", modId, id);
                        return;
                    }

                    modules.put(id, module);

                    Limelight.CONFIG.get().modules.putIfAbsent(id, new LimelightConfig.ModuleConfig());
                });
            } catch (Throwable t) {
                LOGGER.error("{}'s LimelightEntrypoint handler threw an error.", modId, t);
            }
        }

        MODULES_BY_ID.clear();
        MODULES_BY_ID.putAll(modules);

        MODULES = MODULES_BY_ID.values().stream().toList();

        LOGGER.info("Loaded {} Limelight modules.", MODULES_BY_ID.size());
    }

    public static List<LimelightModule> allModules() {
        return MODULES;
    }

    public static List<LimelightModule> enabledModules() {
        // TODO: cache this list instead of remaking it every single time
        List<LimelightModule> enabled = new ArrayList<>(MODULES);
        enabled.removeIf(x -> !Limelight.CONFIG.get().modules.get(x.id()).enabled);
        return enabled;
    }
}
