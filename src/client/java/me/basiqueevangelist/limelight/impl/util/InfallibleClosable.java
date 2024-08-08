package me.basiqueevangelist.limelight.impl.util;

public interface InfallibleClosable extends AutoCloseable {
    @Override
    void close();
}