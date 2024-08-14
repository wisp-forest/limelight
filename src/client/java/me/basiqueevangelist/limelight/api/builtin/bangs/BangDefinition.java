package me.basiqueevangelist.limelight.api.builtin.bangs;

import me.basiqueevangelist.limelight.api.entry.ResultEntryGatherer;
import net.minecraft.text.Text;

/**
 * A bang definition.
 *
 * @param key      the string key used to reference this bang (e.g. {@code mcwiki})
 * @param title    the human-friendly title of this bang
 * @param gatherer the gatherer used to provide results for this bang
 */
public record BangDefinition(String key, Text title, ResultEntryGatherer gatherer) {
}
