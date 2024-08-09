package me.basiqueevangelist.limelight.api.action;

public non-sealed interface ToggleAction extends ResultAction {
    boolean getValue();

    void setValue(boolean value);
}
