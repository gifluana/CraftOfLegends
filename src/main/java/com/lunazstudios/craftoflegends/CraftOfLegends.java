package com.lunazstudios.craftoflegends;

import com.lunazstudios.craftoflegends.control.ServerColState;
import com.lunazstudios.craftoflegends.network.ColNetworking;
import com.lunazstudios.craftoflegends.registry.PacketRegistry;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CraftOfLegends implements ModInitializer {
	public static final String MOD_ID = "craftoflegends";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		PacketRegistry.init();
		ServerColState.init();
		ColNetworking.init();
	}
}