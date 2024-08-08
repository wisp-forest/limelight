package me.basiqueevangelist.limelight.api.entry;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface ResultGatherContext {
    /**
     * @return the raw search text the user typed into the search box
     */
    String searchText();

    /**
     * Checks whether the user's search text matches the given text.
     *
     * @param parts the text parts to match against the search text
     * @return {@code true} if it matches, {@code false} otherwise
     */
    boolean matches(String... parts);

    ClientPlayerEntity player();

    MinecraftClient client();
}
