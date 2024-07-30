package me.basiqueevangelist.flashlight.api;

import net.minecraft.text.Text;

/**
 * Represents a result entry in the Flashlight GUI.
 */
public interface ResultEntry {
    Text categoryName();

    Text text();

    /**
     * Invoked when the entry is run, for example by pressing Enter while focusing it or clicking on it with the mouse.
     */
    void run();
}
