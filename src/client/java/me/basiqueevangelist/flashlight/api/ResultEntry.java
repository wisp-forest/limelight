package me.basiqueevangelist.flashlight.api;

import me.basiqueevangelist.flashlight.api.action.ResultEntryAction;
import net.minecraft.text.Text;

/**
 * Represents a result entry in the Flashlight GUI.
 */
public interface ResultEntry {
    Text categoryName();

    Text text();

    ResultEntryAction action();
}
