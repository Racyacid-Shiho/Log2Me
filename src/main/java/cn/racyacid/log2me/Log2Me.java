package cn.racyacid.log2me;

import cn.racyacid.log2me.helper.LogLevelColorsConfigHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class Log2Me implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("Log2Me");

	@Override
	public void onInitialize() {
		if (!(new File(LogLevelColorsConfigHelper.CONFIG_FILE_PATH).exists())) LogLevelColorsConfigHelper.genConfig();

		LOGGER.info("Mod Log2Me(Version: {}) loaded", FabricLoader.getInstance().getModContainer("log2me").orElseThrow().getMetadata().getVersion().getFriendlyString());
	}
}