package me.basiqueevangelist.limelight.impl.builtin.wiki.source;

import io.wispforest.endec.Endec;
import me.basiqueevangelist.limelight.api.builtin.wiki.WikiSource;
import me.basiqueevangelist.limelight.api.builtin.wiki.WikiSourceType;
import me.basiqueevangelist.limelight.impl.Limelight;
import net.minecraft.registry.Registry;

public class BuiltinWikiSources {
    public static final WikiSourceType<MediaWikiSource> MEDIA_WIKI = register("media_wiki", MediaWikiSource.ENDEC);
    public static final WikiSourceType<MkDocsWikiSource> MKDOCS = register("mkdocs", MkDocsWikiSource.ENDEC);

    private static <T extends WikiSource> WikiSourceType<T> register(String path, Endec<T> endec) {
        return Registry.register(WikiSourceType.REGISTRY, Limelight.id(path), new WikiSourceType<>(endec));
    }

    public static void init() {

    }
}
