package me.basiqueevangelist.limelight.impl.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.api.SyntaxError;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Jankson JANKSON = LimelightConfig.addMarshallers(Jankson.builder()).build();
    private static final Logger LOGGER = LoggerFactory.getLogger("Limelight/ConfigManager");
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("limelight.json5");

    private LimelightConfig config = new LimelightConfig();

    public ConfigManager() {
        load();
    }

    public LimelightConfig get() {
        return config;
    }

    public void load() {
        if (Files.exists(PATH)) {
            try {
                config = JANKSON.fromJson(JANKSON.load(PATH.toFile()), LimelightConfig.class);
            } catch (IOException | SyntaxError e) {
                LOGGER.error("Could not load config file!", e);
            }
        } else {
            save();
        }
    }

    public void save() {
        try {
            try (BufferedWriter bw = Files.newBufferedWriter(PATH)) {
                bw.write(JANKSON.toJson(config).toJson(true, true));
            }
        } catch (IOException e) {
            LOGGER.error("Could not save config file!", e);
        }
    }
}