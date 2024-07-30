package me.basiqueevangelist.flashlight.api.action;

public non-sealed interface InvokeResultEntryAction extends ResultEntryAction {
    /**
     * @return whether the Flashlight screen should be closed before running this action.
     */
    default boolean closesScreen() {
        return true;
    }

    void run();
}
