package io.wispforest.limelight.api.entry;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * A result entry which contain child entries that will show when this entry is selected.
 *
 * @apiNote This entry type is experimental, and may be removed in a later update.
 */
@ApiStatus.Experimental
public non-sealed interface ExpandableResultEntry extends ResultEntry {
    List<ResultEntry> getChildren();
}
