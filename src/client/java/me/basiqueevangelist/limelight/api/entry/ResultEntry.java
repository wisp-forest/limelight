package me.basiqueevangelist.limelight.api.entry;

import me.basiqueevangelist.limelight.api.module.LimelightModule;
import me.basiqueevangelist.limelight.api.action.ResultEntryAction;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Represents a result entry in the Limelight GUI.
 */
public interface ResultEntry {
    /**
     * @return the module that generated this entry
     */
    LimelightModule module();

    /**
     * @return a suitably unique identifier for this entry, used for entry use counting
     */
    Identifier entryId();

    /**
     * @return the text of this entry
     */
    Text text();

    /**
     * @return the type of action to perform when running the entry
     */
    ResultEntryAction action();
}