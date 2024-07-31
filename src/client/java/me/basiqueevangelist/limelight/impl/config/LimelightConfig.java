package me.basiqueevangelist.limelight.impl.config;

import blue.endless.jankson.Comment;
import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class LimelightConfig {
    public static Jankson.Builder addMarshallers(Jankson.Builder builder) {
        return builder
            .registerDeserializer(String.class, Identifier.class, (s, m) -> Identifier.of(s))
            .registerSerializer(Identifier.class, (identifier, marshaller) -> new JsonPrimitive(identifier.toString()));
    }

    @Comment("If true, the Limelight screen will pause the game while in Singleplayer")
    public boolean pauseGameWhileInScreen = false;

    public Map<Identifier, ModuleConfig> modules = new HashMap<>();

    public static class ModuleConfig {
        @Comment("Controls whether everything the module does is enabled.")
        public boolean enabled = true;

        @Comment("Module-specific configuration")
        public JsonObject config = new JsonObject();
    }
}
