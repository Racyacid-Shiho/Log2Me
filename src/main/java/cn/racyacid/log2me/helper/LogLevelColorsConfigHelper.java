package cn.racyacid.log2me.helper;

import cn.racyacid.log2me.Log2Me;
import cn.racyacid.log2me.enums.LogLevelColors;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class LogLevelColorsConfigHelper {
    public static final String CONFIG_FILE_PATH = FabricLoader.getInstance().getConfigDir().toString() + "\\log2chat\\log_level_colors.properties";

    public static void genConfig() {
        Properties properties = new Properties();

        for (LogLevelColors logLevelColor : LogLevelColors.values()) {
            properties.setProperty(logLevelColor.name(), String.valueOf(logLevelColor.color));
        }

        try (FileOutputStream outputStream = new FileOutputStream(CONFIG_FILE_PATH)) {
            properties.store(outputStream, null);
        } catch (IOException e) {
            Log2Me.LOGGER.error("An exception was caught when generating config file: {}", e.getLocalizedMessage());
            return;
        }

        Log2Me.LOGGER.info("Created config file at {}", CONFIG_FILE_PATH);
    }

    public static Map<String, Integer> readConfig() {
        Properties properties = new Properties();
        Map<String, Integer> map = HashMap.newHashMap(8);

        try (FileInputStream inputStream = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(inputStream);
        } catch (IOException e) {
            Log2Me.LOGGER.info("An exception was caught when reading config file: {}", e.getLocalizedMessage());
            return map;
        }

        AtomicBoolean needFix = new AtomicBoolean(false);
        ArrayList<Level> readLevels = new ArrayList<>(8);
        properties.keySet().forEach(key -> {
            String strKey = (String) key;

            Level level = Level.valueOf(strKey);
            readLevels.add(level);

            String color = properties.getProperty(strKey);
            if (color.isEmpty()) {
                color = String.valueOf(LogLevelColors.getDefaultLogLevelColor(level));
                properties.setProperty(strKey, color);
                needFix.set(true);
            }

            map.put(strKey, Integer.valueOf(color));
        });

        ArrayList<Level> needAddLevels = new ArrayList<>(List.of(Level.values()));
        needAddLevels.removeAll(readLevels);
        if (!needAddLevels.isEmpty()) {
            needAddLevels.forEach(level -> properties.setProperty(level.name(), String.valueOf(LogLevelColors.getDefaultLogLevelColor(level))));
            needFix.set(true);
        }

        if (needFix.get()) {
            try (FileOutputStream outputStream = new FileOutputStream(CONFIG_FILE_PATH)) {
                properties.store(outputStream, null);
            } catch (IOException e) {
                Log2Me.LOGGER.error("An exception was caught when writing config file: {}", e.getLocalizedMessage());
            }
        }

        return map;
    }
}