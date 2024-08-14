package me.basiqueevangelist.limelight.impl.resource;

import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public record WikiDescription(Text title, String mediaWikiApi, @Nullable String bangKey,
                              Map<String, LanguageOverride> languageOverrides) {
    public static final Endec<WikiDescription> ENDEC = StructEndecBuilder.of(
        MinecraftEndecs.TEXT.fieldOf("title", WikiDescription::title),
        Endec.STRING.fieldOf("mediaWikiApi", WikiDescription::mediaWikiApi),
        Endec.STRING.nullableOf().optionalFieldOf("bangKey", WikiDescription::bangKey, (String) null),
        LanguageOverride.ENDEC.mapOf().optionalFieldOf("languageOverrides", WikiDescription::languageOverrides, (Map<String, LanguageOverride>) null),
        WikiDescription::new
    );

    public String currentMediaWikiApi() {
        var language = MinecraftClient.getInstance().options.language;

        var override = languageOverrides.get(language);
        if (override == null) return mediaWikiApi();

        if (override.mediaWikiApi() == null) return mediaWikiApi();
        return override.mediaWikiApi();
    }

    public String openSearchUrl(String searchText) {
        return currentMediaWikiApi() + "?action=opensearch&format=json&formatversion=2&limit=10&search=" + URLEncoder.encode(searchText, StandardCharsets.UTF_8);
    }

    public record LanguageOverride(@Nullable String mediaWikiApi) {
        public static final Endec<LanguageOverride> ENDEC = StructEndecBuilder.of(
            Endec.STRING.nullableOf().optionalFieldOf("mediaWikiApi", LanguageOverride::mediaWikiApi, (String) null),

            LanguageOverride::new
        );
    }
}
