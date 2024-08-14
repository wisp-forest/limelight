package me.basiqueevangelist.limelight.api.util;

public interface InfallibleCloseable extends AutoCloseable {
    @Override
    void close();
}