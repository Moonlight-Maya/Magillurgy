package io.github.eninja33.magillurgy;

import io.github.eninja33.magillurgy.content.registrars.BlockEntityRegistrar;
import io.github.eninja33.magillurgy.content.registrars.BlockRegistrar;
import io.github.eninja33.magillurgy.content.registrars.ItemRegistrar;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MagillurgyMod implements ModInitializer {

	public static final String MODID = "magillurgy";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		BlockRegistrar.register();
		BlockEntityRegistrar.register();
		ItemRegistrar.register();
	}
}
