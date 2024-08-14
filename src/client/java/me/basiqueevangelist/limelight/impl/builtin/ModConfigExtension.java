package me.basiqueevangelist.limelight.impl.builtin;

import com.terraformersmc.modmenu.api.ModMenuApi;
import me.basiqueevangelist.limelight.api.builtin.bangs.BangDefinition;
import me.basiqueevangelist.limelight.api.builtin.bangs.BangsProvider;
import me.basiqueevangelist.limelight.api.entry.ResultGatherContext;
import me.basiqueevangelist.limelight.api.extension.LimelightExtension;
import me.basiqueevangelist.limelight.api.entry.ResultEntry;
import me.basiqueevangelist.limelight.api.entry.InvokeResultEntry;
import me.basiqueevangelist.limelight.impl.Limelight;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class ModConfigExtension implements LimelightExtension, BangsProvider {
    public static final Identifier ID = Limelight.id("mod_config");
    public static final ModConfigExtension INSTANCE = new ModConfigExtension();

    private static final Logger LOGGER = LoggerFactory.getLogger("Limelight/ModConfigExtension");
    private static Map<String, Function<Screen, Screen>> MOD_SCREEN_PROVIDERS;

    private ModConfigExtension() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (MOD_SCREEN_PROVIDERS != null) return;
            if (client.getOverlay() != null) return;

            MOD_SCREEN_PROVIDERS = getModScreenProviders();
        });
    }

    @Override
    public Identifier id() {
        return ID;
    }

    @Override
    public void gatherEntries(ResultGatherContext ctx, Consumer<ResultEntry> entryConsumer) {
        for (var provider : MOD_SCREEN_PROVIDERS.entrySet()) {
            var container = FabricLoader.getInstance().getModContainer(provider.getKey()).orElse(null);
            if (container == null) return; // bruh

            var entry = new ModConfigResult(container, provider.getValue());
            if (!ctx.matches(entry.text().getString(), container.getMetadata().getId())) continue;
            entryConsumer.accept(entry);
        }
    }

    @Override
    public List<BangDefinition> bangs() {
        return List.of(new BangDefinition("config", name(), this));
    }

    private record ModConfigResult(ModContainer mod, Function<Screen, Screen> screenProvider) implements InvokeResultEntry {
        @Override
        public LimelightExtension extension() {
            return INSTANCE;
        }

        @Override
        public String entryId() {
            return Limelight.MOD_ID + ":mod_config/" + mod.getMetadata().getId();
        }

        @Override
        public Text text() {
            return Text.literal(mod.getMetadata().getName());
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
            LOGGER.error("Error while loading Mod Menu mod configs. Please report this to the developers of Limelight!", t);
            return Map.of();
        }
    }
}
