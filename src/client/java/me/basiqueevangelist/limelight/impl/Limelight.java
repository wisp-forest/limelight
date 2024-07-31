package me.basiqueevangelist.limelight.impl;

import me.basiqueevangelist.limelight.impl.config.ConfigManager;
import me.basiqueevangelist.limelight.impl.ui.LimelightScreen;
import me.basiqueevangelist.limelight.impl.util.LeastRecentlyUsedList;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class Limelight implements ClientModInitializer {
	public static final String MOD_ID = "limelight";

	public static KeyBinding OPEN_LIMELIGHT = new KeyBinding("key.limelight.open", GLFW.GLFW_KEY_LEFT_BRACKET, KeyBinding.MISC_CATEGORY);

	public static final ConfigManager CONFIG = new ConfigManager();

	public static final LeastRecentlyUsedList<String> ENTRY_USES = new LeastRecentlyUsedList<>(10);

	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(OPEN_LIMELIGHT);

		ModuleManager.init();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!OPEN_LIMELIGHT.wasPressed()) return;
			if (client.player == null) return;

			client.setScreen(new LimelightScreen());
		});
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}