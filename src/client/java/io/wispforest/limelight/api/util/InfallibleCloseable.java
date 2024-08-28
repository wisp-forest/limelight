package io.wispforest.limelight.api.util;

public interface InfallibleCloseable extends AutoCloseable {
    @Override
    void close();
}