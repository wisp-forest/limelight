package me.basiqueevangelist.limelight.impl;

import me.basiqueevangelist.limelight.api.entry.ResultGatherContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class ResultGatherContextImpl implements ResultGatherContext {
    private final String searchText;

    public ResultGatherContextImpl(String searchText) {
        this.searchText = searchText;
    }

    @Override
    public String searchText() {
        return searchText;
    }

    @Override
    public ClientPlayerEntity player() {
        return MinecraftClient.getInstance().player;
    }

    @Override
    public MinecraftClient client() {
        return MinecraftClient.getInstance();
    }
}
