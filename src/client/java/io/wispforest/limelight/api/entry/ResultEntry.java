package io.wispforest.limelight.api.entry;

import io.wispforest.limelight.api.extension.LimelightExtension;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.util.Util;

/**
 * Represents a result entry in the Limelight GUI.
 */
public sealed interface ResultEntry permits InvokeResultEntry, SetSearchTextEntry, ToggleResultEntry, ExpandableResultEntry {
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
     * @return the prefix used to identify the extension in the search result.
     * @apiNote By default, it'll use the translation if it exists, if it doesn't, it'll use the extension's name
     * */
    default Text prefix() {
        String key = Util.createTranslationKey("limelightExtension", extension().id()) + ".prefix";
        if (Language.getInstance().hasTranslation(key))
            return Text.translatable(key);
        return extension().name();
    }
}
