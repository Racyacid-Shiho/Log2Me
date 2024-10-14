package cn.racyacid.log2me.enums;

import org.apache.logging.log4j.Level;

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

    public static int getLogLevelColor(Level level) {
        if (level == Level.OFF)   return OFF.color;
        if (level == Level.FATAL) return FATAL.color;
        if (level == Level.ERROR) return ERROR.color;
        if (level == Level.WARN)  return WARN.color;
        if (level == Level.INFO)  return INFO.color;
        if (level == Level.DEBUG) return DEBUG.color;
        if (level == Level.TRACE) return TRACE.color;
        if (level == Level.ALL)   return ALL.color;
        throw new IllegalArgumentException("Unknown log level: " + level.toString());
    }
}