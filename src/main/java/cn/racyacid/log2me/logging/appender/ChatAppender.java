package cn.racyacid.log2me.logging.appender;

import cn.racyacid.log2me.enums.LogLevelColors;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;

public class ChatAppender extends AbstractAppender {
    /** U+200A is the "Hair Space", right there ->" "<- */
    public static String LOG_IDENTIFIER = "\u200a";

    public static @NotNull Level globalLogLevel = Level.INFO;

    public static final HashSet<ServerPlayerEntity> GETTING_GLOBAL_LOG_PLAYERS = new HashSet<>();
    public static final HashMap<ServerPlayerEntity, Pair<Boolean, Level>> PRIVATE_LOG_LEVEL_PLAYERS = new HashMap<>();

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("HH:mm:ss");

    static {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        ChatAppender chatAppender = new ChatAppender("ChatAppender", null, PatternLayout.createDefaultLayout());
        chatAppender.start();
        context.getConfiguration().getRootLogger().addAppender(chatAppender, Level.ALL, null);
        context.updateLoggers();
    }

    private ChatAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout, false, Property.EMPTY_ARRAY);
    }

    @Override
    public void append(LogEvent event) {
        Level logLevel = event.getLevel();

        if (!PRIVATE_LOG_LEVEL_PLAYERS.isEmpty()) PRIVATE_LOG_LEVEL_PLAYERS.forEach((player, pair) -> {
            if (pair.getLeft() && logLevel.isMoreSpecificThan(pair.getRight()))
                player.sendMessage(getChatLog(event));
        });

        if (!GETTING_GLOBAL_LOG_PLAYERS.isEmpty() && logLevel.isMoreSpecificThan(globalLogLevel))
            GETTING_GLOBAL_LOG_PLAYERS.forEach(player -> player.sendMessage(getChatLog(event), false));
    }

    private static Text getChatLog(LogEvent event) {
        Level level = event.getLevel();
        return Text.literal(String.format("[%s] [%s] %s",
               DATE_FORMATTER.format(event.getTimeMillis()),
               level.toString(),
               event.getMessage().getFormattedMessage()) + LOG_IDENTIFIER)
                .withColor(LogLevelColors.getConfigLogLevelColor(level));
    }
}