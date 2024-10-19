package cn.racyacid.log2me.helper;

import cn.racyacid.log2me.enums.LogLevelColors;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CommandHelper {
    public static final SimpleCommandExceptionType WANNA_SET_GLOBAL_LEVEL_TO_GLOBAL_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("log2me.exception.wanna_set_global_level_to_global"));
    public static final SimpleCommandExceptionType UNKNOWN_LOG_LEVEL_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("log2me.exception.unknown_log_level"));

    public static final SuggestionProvider<ServerCommandSource> LOG_LEVEL_SUGGESTIONS = SuggestionProviders.register(Identifier.of("log2me", "log_levels"), (context, builder) -> {
        for (LogLevelColors level : LogLevelColors.values()) builder.suggest(level.name());
        builder.suggest("GLOBAL");
        return builder.buildFuture();
    });
}