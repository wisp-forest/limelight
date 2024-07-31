package me.basiqueevangelist.flashlight.api.entry;

import me.basiqueevangelist.flashlight.api.module.FlashlightModule;
import me.basiqueevangelist.flashlight.api.action.ResultEntryAction;
import net.minecraft.text.Text;

/**
 * Represents a result entry in the Flashlight GUI.
 */
public interface ResultEntry {
    /**
     * @return the module that generated this entry
     */
    FlashlightModule module();

    /**
     * @return the text of this entry
     */
    Text text();

    /**
     * @return the type of action to perform when running the entry
     */
    ResultEntryAction action();
}
