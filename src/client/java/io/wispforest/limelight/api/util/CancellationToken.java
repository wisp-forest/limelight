package io.wispforest.limelight.api.util;

import java.lang.ref.WeakReference;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

/**
 * A token used for cooperative cancellation of asynchronous tasks.
 *
 * @see <a href="https://learn.microsoft.com/en-us/dotnet/api/system.threading.cancellationtoken">System.Threading.CancellationToken</a>
 */
public class CancellationToken {
    /**
     * An empty cancellation token that will never be cancelled.
     */
    public static final CancellationToken NONE = new CancellationToken();

    private final WeakReference<CancellationTokenSource> sourceRef;
    boolean cancelled = false;

    CancellationToken(CancellationTokenSource source) {
        this.sourceRef = new WeakReference<>(source);
    }

    private CancellationToken() {
        this.sourceRef = new WeakReference<>(null);
        this.cancelled = false;
    }

    /**
     * {@return whether this token has been cancelled}
     */
    public boolean cancelled() {
        return cancelled;
    }

    /**
     * Throws if this token is cancelled.
     *
     * @throws CancellationException if this token is cancelled
     */
    public void throwIfCancelled() {
        if (cancelled())
            throw new CancellationException();
    }

    /**
     * Wraps a future, cancelling it when this token is cancelled.
     * @param original the future to wrap
     * @return the original future
     */
    public <T> CompletableFuture<T> wrapFuture(CompletableFuture<T> original) {
        if (!original.isDone()) {
            var subscription = register(() -> {
                if (cancelled()) original.cancel(true);
            });

            original.whenComplete((unused1, unused2) -> {
                subscription.close();
            });
        }

        return original;
    }

    /**
     * Registers a callback invoked when this token is cancelled
     * @param action the action to run when this token is cancelled
     * @return an object that, if closed, unregisters the callback
     */
    public InfallibleCloseable register(Runnable action) {
        if (cancelled) {
            action.run();
            return Subscription.EMPTY;
        }

        CancellationTokenSource source = sourceRef.get();

        if (source == null) return Subscription.EMPTY;

        synchronized (source.lock) {
            if (source.listeners == null) {
                action.run();
                return Subscription.EMPTY;
            }

            source.listeners.add(action);
            return new Subscription(this, action);
        }
    }

    private static class Subscription implements InfallibleCloseable {
        private static final Subscription EMPTY = new Subscription(null, null);
        private CancellationToken token;
        private Runnable runnable;

        private Subscription(CancellationToken token, Runnable runnable) {
            this.token = token;
            this.runnable = runnable;
        }

        @Override
        public void close() {
            if (token != null) {
                if (token.cancelled()) {
                    token = null;
                    runnable = null;
                    return;
                }

                var source = token.sourceRef.get();

                if (source == null) {
                    token = null;
                    runnable = null;
                    return;
                }

                synchronized (source.lock) {
                    if (source.listeners == null) {
                        token = null;
                        runnable = null;
                        return;
                    }

                    source.listeners.remove(runnable);
                    token = null;
                    runnable = null;
                }
            }
        }
    }
}