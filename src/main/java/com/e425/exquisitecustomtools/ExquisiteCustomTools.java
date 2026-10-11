package com.e425.exquisitecustomtools;

import com.e425.exquisitecustomtools.event.FellingHandler;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExquisiteCustomTools implements ModInitializer {
	public static final String MOD_ID = "exquisite-custom-tools";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModDataComponents.register();
		FellingHandler.register();

		LOGGER.info("425s Custom Tools Loaded!");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
