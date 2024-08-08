package me.basiqueevangelist.limelight.api.entry;

import me.basiqueevangelist.limelight.impl.util.CancellationToken;
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

    /**
     * @return a token that will be cancelled when the search results are refreshed
     */
    CancellationToken cancellationToken();

    ClientPlayerEntity player();

    MinecraftClient client();
}
