package io.wispforest.limelight.api.builtin.bangs;

import java.util.List;

/**
 * Marks a Limelight extension as providing bangs.
 */
public interface BangsProvider {
    /**
     * @return the list of bangs provided by this extension
     * @apiNote This method's return value isn't cached, and can change between calls and depend on resource pack data.
     */
    List<BangDefinition> bangs();
}
