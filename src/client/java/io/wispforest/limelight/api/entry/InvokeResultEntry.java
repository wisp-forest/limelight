package io.wispforest.limelight.api.entry;

/**
 * A result entry which invokes a method when run.
 */
public non-sealed interface InvokeResultEntry extends ResultEntry {
    /**
     * @return whether the Limelight screen should be closed before running this entry.
     */
    default boolean closesScreen() {
        return true;
    }

    /**
     * Runs the action associated with this entry.
     */
    void run();
}
