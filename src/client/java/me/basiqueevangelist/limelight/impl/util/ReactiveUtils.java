package me.basiqueevangelist.limelight.impl.util;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public final class ReactiveUtils {
    private ReactiveUtils() {

    }

    public static <T> Observable<T> throttle(Observable<T> wrapped, long delayNanos, Executor executor) {
        return new Observable<>(wrapped.get()) {
            private long previousUpdate = 0;
            private boolean taskSent = false;

            {
                wrapped.observe(newValue -> {
                    long now = System.nanoTime();
                    long passed = now - previousUpdate;

                    if (delayNanos > passed) {
                        if (taskSent) return;

                        taskSent = true;
                        CompletableFuture.delayedExecutor(delayNanos, TimeUnit.NANOSECONDS, executor)
                            .execute(() -> {
                                set(wrapped.get());
                                previousUpdate = System.nanoTime();
                                taskSent = false;
                            });
                    } else {
                        executor.execute(() -> set(newValue));
                        previousUpdate = now;
                    }
                });
            }
        };
    }

    public static <E, T> Observable<E> map(Observable<T> source, Function<T, E> mapper) {
        return new Observable<>(mapper.apply(source.get())) {
            {
                source.observe(x -> super.set(mapper.apply(x)));
            }
        };
    }
    
    public static <T> Observable<T> from(Option<T> option) {
        Observable<T> o = Observable.of(option.value());
        option.observe(o::set);
        return o;
    }
}