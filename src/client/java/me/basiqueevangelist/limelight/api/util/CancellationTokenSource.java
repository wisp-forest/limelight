package me.basiqueevangelist.limelight.api.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a {@link CancellationToken} and allows cancelling it.
 *
 * @see <a href="https://learn.microsoft.com/en-us/dotnet/api/system.threading.cancellationtokensource">System.Threading.CancellationTokenSource</a>
 */
public class CancellationTokenSource {
    List<Runnable> listeners = new ArrayList<>();
    private final CancellationToken token = new CancellationToken(this);
    final Object lock = new Object(); // bruh

    /**
     * {@return the token linked to this source}
     */
    public CancellationToken token() {
        return token;
    }

    /**
     * Immediately cancels this cancellation token source.
     */
    public void cancel() {
        if (token.cancelled) return;

        synchronized (lock) {
            if (token.cancelled) return;

            token.cancelled = true;

            for (Runnable r : listeners) {
                r.run();
            }

            listeners.clear();
            listeners = null;
        }
    }
}