package me.basiqueevangelist.limelight.api.action;

public non-sealed interface InvokeAction extends ResultAction {
    /**
     * @return whether the Limelight screen should be closed before running this action.
     */
    default boolean closesScreen() {
        return true;
    }

    void run();
}
