package cn.racyacid.log2me;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Log2Me implements ModInitializer {
	private static final FabricLoader INSTANCE = FabricLoader.getInstance();
	public static final Logger LOGGER = LogManager.getLogger("Log2Me");

	@Override
	public void onInitialize() {
		LOGGER.info("Mod Log2Me(Version: {}) loaded", INSTANCE.getModContainer("log2me").orElseThrow().getMetadata().getVersion().getFriendlyString());
	}
}