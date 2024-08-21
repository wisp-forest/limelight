package me.basiqueevangelist.limelight.impl.resource.wiki;

import com.mojang.serialization.Lifecycle;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import me.basiqueevangelist.limelight.impl.Limelight;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

public record WikiSourceType<T extends WikiSource>(StructEndec<WikiDescription<T>> descriptionEndec) {
    public static final Registry<WikiSourceType<?>> REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(Limelight.id("wiki_source_type")), Lifecycle.stable());

    public static <T extends WikiSource> WikiSourceType<T> create(Endec<T> endec) {
        return new WikiSourceType<>(WikiDescription.createEndec(endec));
    }

    public static final WikiSourceType<MediaWikiSource> MEDIA_WIKI = register("media_wiki", MediaWikiSource.ENDEC);
    public static final WikiSourceType<MkDocsWikiSource> MKDOCS = register("mkdocs", MkDocsWikiSource.ENDEC);

    private static <T extends WikiSource> WikiSourceType<T> register(String path, Endec<T> endec) {
        return Registry.register(REGISTRY, Limelight.id(path), create(endec));
    }
}
