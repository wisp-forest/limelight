package me.basiqueevangelist.limelight.impl.resource.wiki;

import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record WikiDescription<T extends WikiSource>(Text title, @Nullable String bangKey, T source,
                                                    Map<String, T> languageOverrides) {
    public static final Endec<WikiDescription<?>> ENDEC = Endec.dispatchedStruct(
            WikiSourceType::descriptionEndec, d -> d.source().id(), MinecraftEndecs.ofRegistry(WikiSourceType.REGISTRY));

    public static <T extends WikiSource> StructEndec<WikiDescription<T>> createEndec(Endec<T> sourceEndec) {
        return StructEndecBuilder.of(
                MinecraftEndecs.TEXT.fieldOf("title", WikiDescription::title),
                Endec.STRING.nullableOf().optionalFieldOf("bang_key", WikiDescription::bangKey, (String) null),
                sourceEndec.fieldOf("source", WikiDescription::source),
                sourceEndec.mapOf().optionalFieldOf("language_overrides", WikiDescription::languageOverrides, Map.of()),
                WikiDescription::new
        );
    }

    public String createSearchUrl(String query) {
        var language = MinecraftClient.getInstance().options.language;

        var override = languageOverrides.get(language);
        if (override == null) return source.createSearchUrl(query);

        return override.createSearchUrl(query);
    }
}
