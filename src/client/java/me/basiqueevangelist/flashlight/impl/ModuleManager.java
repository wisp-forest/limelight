package me.basiqueevangelist.flashlight.impl;

import me.basiqueevangelist.flashlight.api.FlashlightEntrypoint;
import me.basiqueevangelist.flashlight.api.module.FlashlightModule;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ModuleManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("Flashlight/ModuleManager");
    private static final Map<Identifier, FlashlightModule> MODULES_BY_ID = new HashMap<>();
    private static List<FlashlightModule> MODULES;

    private ModuleManager() {

    }

    public static void init() {
        var entrypoints = FabricLoader.getInstance().getEntrypointContainers(FlashlightEntrypoint.KEY, FlashlightEntrypoint.class);
        Map<Identifier, FlashlightModule> modules = new HashMap<>();

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
                });
            } catch (Throwable t) {
                LOGGER.error("{}'s FlashlightEntrypoint handler threw an error.", modId, t);
            }
        }

        MODULES_BY_ID.clear();
        MODULES_BY_ID.putAll(modules);

        MODULES = MODULES_BY_ID.values().stream().toList();

        LOGGER.info("Loaded {} Flashlight modules.", MODULES_BY_ID.size());
    }

    public static List<FlashlightModule> allModules() {
        return MODULES;
    }
}
