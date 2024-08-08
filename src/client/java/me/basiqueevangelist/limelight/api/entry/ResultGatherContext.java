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

    ClientPlayerEntity player();

    MinecraftClient client();
}
