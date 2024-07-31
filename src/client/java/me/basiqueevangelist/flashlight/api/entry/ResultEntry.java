package me.basiqueevangelist.flashlight.api.entry;

import me.basiqueevangelist.flashlight.api.module.FlashlightModule;
import me.basiqueevangelist.flashlight.api.action.ResultEntryAction;
import net.minecraft.text.Text;

/**
 * Represents a result entry in the Flashlight GUI.
 */
public interface ResultEntry {
    FlashlightModule module();

    Text text();

    ResultEntryAction action();
}
