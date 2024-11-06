package cn.racyacid.log2me.enums;

import cn.racyacid.log2me.helper.LogLevelColorsConfigHelper;
import org.apache.logging.log4j.Level;

import java.util.Map;

public enum LogLevelColors {
    /** Gray */
    OFF(0x808080),

    /** Dark Red */
    FATAL(0x8B0000),

    /** Red */
    ERROR(0xFF0000),

    /** Yellow */
    WARN(0xFFFF00),

    /** Green */
    INFO(0x00FF00),

    /** Pale Green */
    DEBUG(0x98FB98),

    /** Cyan */
    TRACE(0x00FFFF),

    /** Purple */
    ALL(0x800080);

    public final int color;

    LogLevelColors(int color) {
        this.color = color;
    }

    private static final Map<String, Integer> LOG_LEVEL_COLOR_MAP;

    public static int getConfigLogLevelColor(Level level) {
        return LOG_LEVEL_COLOR_MAP.get(level.toString());
    }

    static {
        LOG_LEVEL_COLOR_MAP = LogLevelColorsConfigHelper.readConfig();
    }
}