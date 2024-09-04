package io.wispforest.limelight.impl.util;

import java.util.concurrent.CancellationException;

public final class ExceptionUtils {
    private ExceptionUtils() { }

    public static boolean isCancellation(Throwable t) {
        if (t instanceof CancellationException) return true;
        else if (t.getCause() != null) return isCancellation(t.getCause());
        else return false;
    }
}
