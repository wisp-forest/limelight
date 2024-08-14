package me.basiqueevangelist.limelight.api.builtin.bangs;

import java.util.List;

/**
 * Marks a Limelight module as providing bangs.
 */
public interface BangsProvider {
    /**
     * @return the list of bangs provided by this module
     * @apiNote This method's return value isn't cached, and can change between calls and depend on resource pack data.
     */
    List<BangDefinition> bangs();
}
