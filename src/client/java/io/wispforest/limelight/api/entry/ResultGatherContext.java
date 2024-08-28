package io.wispforest.limelight.api.entry;

import io.wispforest.limelight.api.util.CancellationToken;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;

@ApiStatus.NonExtendable
public interface ResultGatherContext {
    /**
     * @return the raw search text the user typed into the search box
     */
    String searchText();

    /**
     * @return creates a result gather context with different search text
     */
    ResultGatherContext withSearchText(String searchText);

    /**
     * Checks whether the user's search text matches the given text.
     *
     * @param parts the text parts to match against the search text
     * @return {@code true} if it matches, {@code false} otherwise
     */
    boolean matches(String... parts);

    /**
     * Starts tracking a future. While any tracked future is pending, Limelight will show a progress indicator (WIP),
     * and any future that rejects will show the error in the log. Additionally, all tracked futures are cancelled when
     * {@link #cancellationToken()} is cancelled.
     *
     * @param future the future track
     */
    void trackFuture(CompletableFuture<?> future);

    /**
     * @return a token that will be cancelled when the search results are refreshed
     */
    CancellationToken cancellationToken();

    /**
     * @return the player that opened the screen
     */
    ClientPlayerEntity player();

    /**
     * @return the current Minecraft client instance
     */
    MinecraftClient client();
}
