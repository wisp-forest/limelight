package me.basiqueevangelist.limelight.api.entry;

/**
 * A result entry which sets the screen's search text when run.
 */
public non-sealed interface SetSearchTextEntry extends ResultEntry {
    String newSearchText();
}
