package com.lunazstudios.craftoflegends;

import com.lunazstudios.craftoflegends.bbmodel.BbModelLoader;
import com.lunazstudios.craftoflegends.hud.ColAbilitiesHud;
import com.lunazstudios.craftoflegends.networking.ColClientNetworking;
import com.lunazstudios.craftoflegends.registry.KeybindRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class CraftOfLegendsClient implements ClientModInitializer {
	public static final String DEFAULT_BBMODEL_PATH = "craftoflegends:bbmodels/cube.bbmodel";

	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register(ColAbilitiesHud::render);
		BbModelLoader.setDefaultModelId(DEFAULT_BBMODEL_PATH);
		ColClientNetworking.init();
		KeybindRegistry.registerKeybinds();
		KeybindRegistry.registerKeybindHandlers();
	}
}