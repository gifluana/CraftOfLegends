package com.lunazstudios.craftoflegends;

import com.lunazstudios.craftoflegends.bbmodel.BbModelLoader;
import net.fabricmc.api.ClientModInitializer;

public class CraftOfLegendsClient implements ClientModInitializer {
	public static final String DEFAULT_BBMODEL_PATH = "craftoflegends:bbmodels/cube.bbmodel";

	@Override
	public void onInitializeClient() {
		BbModelLoader.setDefaultModelId(DEFAULT_BBMODEL_PATH);
	}
}