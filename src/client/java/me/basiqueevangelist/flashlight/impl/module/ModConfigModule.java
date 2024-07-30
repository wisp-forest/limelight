package me.basiqueevangelist.flashlight.impl.module;

import com.terraformersmc.modmenu.api.ModMenuApi;
import me.basiqueevangelist.flashlight.api.ResultEntry;
import me.basiqueevangelist.flashlight.api.ResultGatherEvents;
import me.basiqueevangelist.flashlight.api.action.InvokeResultEntryAction;
import me.basiqueevangelist.flashlight.api.action.ResultEntryAction;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ModConfigModule {
    private static final Logger LOGGER = LoggerFactory.getLogger("Flashlight/ModConfigModule");

    private static Map<String, Function<Screen, Screen>> MOD_SCREEN_PROVIDERS;

    public static final Text CATEGORY = Text.translatable("flashlightCategory.flashlight.modconfig");

    public static void init() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (MOD_SCREEN_PROVIDERS != null) return;
            if (client.getOverlay() != null) return;

            MOD_SCREEN_PROVIDERS = getModScreenProviders();
        });

        ResultGatherEvents.GATHER.register((searchText, entryConsumer) -> {
            for (var provider : MOD_SCREEN_PROVIDERS.entrySet()) {
                var container = FabricLoader.getInstance().getModContainer(provider.getKey()).orElse(null);
                if (container == null) return; // bruh

                var entry = new ModConfigResult(container, provider.getValue());
                if (!(StringUtils.containsIgnoreCase(entry.text().getString(), searchText)
                  || StringUtils.containsIgnoreCase(container.getMetadata().getId(), searchText))) continue;
                entryConsumer.accept(entry);
            }
        });
    }

    private record ModConfigResult(ModContainer mod, Function<Screen, Screen> screenProvider) implements ResultEntry, InvokeResultEntryAction {
        @Override
        public Text categoryName() {
            return CATEGORY;
        }

        @Override
        public Text text() {
            return Text.literal(mod.getMetadata().getName());
        }

        @Override
        public ResultEntryAction action() {
            return this;
        }

        @Override
        public void run() {
            MinecraftClient.getInstance().setScreen(screenProvider.apply(null));
        }
    }

    private static Map<String, Function<Screen, Screen>> getModScreenProviders() {
        try {
            Map<String, Function<Screen, Screen>> map = new HashMap<>();

            for (EntrypointContainer<ModMenuApi> entrypoint : FabricLoader.getInstance().getEntrypointContainers("modmenu", ModMenuApi.class)) {
                String modId = entrypoint.getProvider().getMetadata().getId();

                try {
                    ModMenuApi api = entrypoint.getEntrypoint();

                    // Yes, Mod Menu's API is so bad that I have to instantiate screens to figure out if a mod has one.
                    api.getProvidedConfigScreenFactories().forEach((otherModId, factory) -> {
                        var testScreen = factory.create(null);
                        if (testScreen != null) map.put(otherModId, factory::create);
                    });

                    var testScreen = api.getModConfigScreenFactory().create(null);
                    if (testScreen != null) map.put(modId, api.getModConfigScreenFactory()::create);
                } catch (Throwable e) {
                    LOGGER.error("Mod {}'s ModMenuApi implementation threw an error, ignoring", modId, e);
                }
            }

            return map;
        } catch (Throwable t) {
            LOGGER.error("Error while loading Mod Menu mod configs. Please report this to the developers of Flashlight!", t);
            return Map.of();
        }
    }
}
