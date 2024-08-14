package me.basiqueevangelist.limelight.api.entry;

import org.jetbrains.annotations.ApiStatus;

/**
 * A result entry which represents a toggleable value.
 *
 * @apiNote This entry type is experimental, and may be removed in a later update.
 */
@ApiStatus.Experimental
public non-sealed interface ToggleResultEntry extends ResultEntry {
    boolean getValue();

    void setValue(boolean value);
}
