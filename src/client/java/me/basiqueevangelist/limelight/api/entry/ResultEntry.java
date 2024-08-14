package me.basiqueevangelist.limelight.api.entry;

import me.basiqueevangelist.limelight.api.extension.LimelightExtension;
import me.basiqueevangelist.limelight.api.action.ResultAction;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Represents a result entry in the Limelight GUI.
 */
public interface ResultEntry {
    /**
     * @return the extension that generated this entry
     */
    LimelightExtension extension();

    /**
     * @return a suitably unique identifier for this entry, used for entry use counting
     * @apiNote This should be formatted like a Minecraft {@link Identifier}, but is intentionally not one so that you
     * can attach arbitrary string data.
     */
    String entryId();

    /**
     * @return the text of this entry
     */
    Text text();

    /**
     * @return the type of action to perform when running the entry
     */
    ResultAction action();
}
