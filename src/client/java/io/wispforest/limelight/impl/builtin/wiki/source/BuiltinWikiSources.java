package io.wispforest.limelight.impl.builtin.wiki.source;

import io.wispforest.endec.Endec;
import io.wispforest.limelight.api.builtin.wiki.WikiSource;
import io.wispforest.limelight.api.builtin.wiki.WikiSourceType;
import io.wispforest.limelight.impl.Limelight;
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
