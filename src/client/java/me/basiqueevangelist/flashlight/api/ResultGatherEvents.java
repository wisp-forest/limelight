package me.basiqueevangelist.flashlight.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.function.Consumer;

public final class ResultGatherEvents {
    private ResultGatherEvents() {

    }

    /**
     * An event for registering exclusive entry gatherers. Handlers should return true if the search text indicates that
     * only this handler should be used.
     * <p>
     * For example, if you are making a calculator module, you may use this event to register a handler that takes over
     * entry gathering if the search text starts with {@code =}
     */
    public static final Event<ExclusiveGather> EXCLUSIVE_GATHER = EventFactory.createArrayBacked(ExclusiveGather.class, handlers -> (searchText, entryConsumer) -> {
        for (var handler : handlers) {
            if (handler.onExclusiveEntryGather(searchText, entryConsumer)) return true;
        }

        return false;
    });

    /**
     * Generic event for gathering result entries. Runs after {@link #EXCLUSIVE_GATHER}.
     */
    public static final Event<Gather> GATHER = EventFactory.createArrayBacked(Gather.class, handlers -> (searchText, entryConsumer) -> {
        for (var handler : handlers) {
            handler.onEntryGather(searchText, entryConsumer);
        }
    });

    public interface ExclusiveGather {
        /**
         * Invoked when result entries are to be gathered for the Flashlight popup.
         *
         * @return {@code true} if this handler should handle this search exclusively
         */
        boolean onExclusiveEntryGather(String searchText, Consumer<ResultEntry> entryConsumer);
    }

    public interface Gather {
        /**
         * Invoked when result entries are to be gathered for the Flashlight popup.
         */
        void onEntryGather(String searchText, Consumer<ResultEntry> entryConsumer);
    }
}
