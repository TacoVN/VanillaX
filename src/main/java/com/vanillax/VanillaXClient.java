package com.vanillax;

import net.fabricmc.api.ClientModInitializer;

public class VanillaXClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		VanillaXConfig.load();
	}
}
