package me.basiqueevangelist.flashlight.impl;

import me.basiqueevangelist.flashlight.impl.module.KeyBindingsModule;
import me.basiqueevangelist.flashlight.impl.module.ModConfigModule;
import me.basiqueevangelist.flashlight.impl.ui.FlashlightScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class Flashlight implements ClientModInitializer {
	public static KeyBinding OPEN_FLASHLIGHT = new KeyBinding("key.flashlight.open", GLFW.GLFW_KEY_LEFT_BRACKET, KeyBinding.MISC_CATEGORY);

	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(OPEN_FLASHLIGHT);

		KeyBindingsModule.init();
		if (FabricLoader.getInstance().isModLoaded("modmenu")) ModConfigModule.init();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!OPEN_FLASHLIGHT.wasPressed()) return;
			if (client.player == null) return;

			client.setScreen(new FlashlightScreen());
		});
	}
}