package cn.racyacid.log2me.command;

import cn.racyacid.log2me.helper.CommandHelper;
import cn.racyacid.log2me.logging.appender.ChatAppender;
import cn.racyacid.log2me.util.TextUtils;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static cn.racyacid.log2me.logging.appender.ChatAppender.*;
import static net.minecraft.command.argument.EntityArgumentType.getPlayers;
import static net.minecraft.command.argument.EntityArgumentType.players;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class LoggingCommand implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("log2me").requires(source -> source.hasPermissionLevel(4))
                        .then(literal("start").then(argument("player", players()).executes(context -> {
                            ArrayList<String> playerNames = startLog2Players(getPlayers(context, "player"));

                            ServerCommandSource source = context.getSource();
                            source.sendFeedback(() -> Text.translatable("log2me.logging.start.send_to_target_players", playerNames.size(), StringUtils.join(playerNames)), true);

                            return 1;
                        }))).then(literal("stop").then(argument("player", players()).executes(context -> {
                            ArrayList<String> playerNames = stopLog2Players(getPlayers(context, "player"));

                            ServerCommandSource source = context.getSource();
                            source.sendFeedback(() -> Text.translatable("log2me.logging.stop.send_to_target_players", playerNames.size(), StringUtils.join(playerNames)), true);

                            return 1;
                        }))).then(literal("setGlobalLevel").then(argument("level", StringArgumentType.word()).suggests(CommandHelper.LOG_LEVEL_SUGGESTIONS).executes(context -> {
                            String strLevel = StringArgumentType.getString(context, "level");
                            if (strLevel.equalsIgnoreCase("GLOBAL")) throw CommandHelper.WANNA_SET_GLOBAL_LEVEL_TO_GLOBAL_EXCEPTION.create();

                            Level level = Level.toLevel(strLevel, null);
                            if (level == null) throw CommandHelper.UNKNOWN_LOG_LEVEL_EXCEPTION.create();

                            globalLogLevel = level;

                            context.getSource().sendFeedback(() -> Text.translatable("log2me.logging.level.set_global_level", TextUtils.getStyledLogLevel(level)), true);

                            return 1;
                        }))).then(literal("toGlobal").then(argument("player", players()).executes(context -> {
                            Collection<ServerPlayerEntity> players = getPlayers(context, "player");

                            setPrivate2Global(players);

                            context.getSource().sendFeedback(() -> Text.translatable("log2me.logging.level.set_private_to_global", players.size(), TextUtils.getStyledLogLevel(globalLogLevel), TextUtils.getStringNames(players)), true);

                            return 1;
                        }))).then(literal("removePrivate").then(argument("player", players()).executes(context -> {
                            ArrayList<String> playerNames = removeFromPrivate(getPlayers(context, "player"));

                            context.getSource().sendFeedback(() -> Text.translatable("log2me.logging.players.private.remove", playerNames.size(), StringUtils.join(playerNames)), true);

                            return 1;
                        }))).then(literal("setPrivateLevel").then(argument("player", players()).then(argument("level", StringArgumentType.word()).suggests(CommandHelper.LOG_LEVEL_SUGGESTIONS).executes(context -> {
                            String strLevel = StringArgumentType.getString(context, "level");
                            boolean isGlobal = strLevel.equalsIgnoreCase("GLOBAL");

                            Level level = isGlobal ? globalLogLevel : Level.toLevel(strLevel, null);
                            if (level == null) throw CommandHelper.UNKNOWN_LOG_LEVEL_EXCEPTION.create();

                            Collection<ServerPlayerEntity> players = getPlayers(context, "player");

                            add2PrivateOrModify(players, level, false);

                            context.getSource().sendFeedback(() -> Text.translatable("log2me.logging.level.set_private_level", players.size(), TextUtils.getStyledLogLevel(level), TextUtils.getStringNames(players)), true);

                            return 1;
                        }).then(argument("startLog", BoolArgumentType.bool()).executes(context -> {
                            String strLevel = StringArgumentType.getString(context, "level");
                            boolean isGlobal = strLevel.equalsIgnoreCase("GLOBAL");

                            Level level = isGlobal ? globalLogLevel : Level.toLevel(strLevel, null);
                            if (level == null) throw CommandHelper.UNKNOWN_LOG_LEVEL_EXCEPTION.create();

                            Collection<ServerPlayerEntity> players = getPlayers(context, "player");

                            add2PrivateOrModify(players, level, BoolArgumentType.getBool(context, "startLog"));

                            context.getSource().sendFeedback(() -> Text.translatable("log2me.logging.level.set_private_level", players.size(), TextUtils.getStyledLogLevel(level), TextUtils.getStringNames(players)), true);

                            return 1;
                        })))))
                )
        );

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("loggingList").executes(context -> {
                    ServerCommandSource source = context.getSource();
                    source.sendFeedback(() -> Text.translatable("log2me.logging.players.global", TextUtils.getStyledLogLevel(globalLogLevel), TextUtils.getStringNames(ChatAppender.GETTING_GLOBAL_LOG_PLAYERS)), false);
                    source.sendFeedback(Text::empty, false);
                    source.sendFeedback(() -> Text.translatable("log2me.logging.players.private"), false);
                    source.sendFeedback(() -> Text.translatable("log2me.logging.players.private.getting", TextUtils.getStyledLogLevelByMap(PRIVATE_LOG_LEVEL_PLAYERS, true)), false);
                    source.sendFeedback(() -> Text.translatable("log2me.logging.players.private.not_getting", TextUtils.getStyledLogLevelByMap(PRIVATE_LOG_LEVEL_PLAYERS, false)), false);
                    return 1;
                }))
        );
    }

    private ArrayList<String> startLog2Players(Collection<ServerPlayerEntity> players) {
        ArrayList<String> list = new ArrayList<>();

        if (PRIVATE_LOG_LEVEL_PLAYERS.keySet().containsAll(players))
            PRIVATE_LOG_LEVEL_PLAYERS.forEach((player, pair) -> {
                if (players.contains(player)) {
                    pair.setLeft(true);
                    players.remove(player);
                    list.add(player.getNameForScoreboard());
                }
            });

        players.forEach(player -> {
            if (!GETTING_GLOBAL_LOG_PLAYERS.contains(player)) {
                GETTING_GLOBAL_LOG_PLAYERS.add(player);
                list.add(player.getNameForScoreboard());
            }
        });

        return list;
    }

    private ArrayList<String> stopLog2Players(Collection<ServerPlayerEntity> players) {
        ArrayList<String> list = new ArrayList<>();

        if (PRIVATE_LOG_LEVEL_PLAYERS.keySet().containsAll(players))
            PRIVATE_LOG_LEVEL_PLAYERS.forEach((player, pair) -> {
                if (players.contains(player)) {
                    pair.setLeft(false);
                    players.remove(player);
                    list.add(player.getNameForScoreboard());
                }
            });

        players.forEach(player -> {
            if (!GETTING_GLOBAL_LOG_PLAYERS.contains(player)) {
                GETTING_GLOBAL_LOG_PLAYERS.remove(player);
                list.add(player.getNameForScoreboard());
            }
        });

        return list;
    }

    private void add2PrivateOrModify(Collection<ServerPlayerEntity> players, Level level, boolean startLog) {
        HashMap<ServerPlayerEntity, Pair<Boolean, Level>> shouldAddMap = new HashMap<>();
        Pair<Boolean, Level> value = new Pair<>(startLog, level);

        players.forEach(player -> {
            PRIVATE_LOG_LEVEL_PLAYERS.computeIfPresent(player, (playerB, pair) -> {
                pair.setRight(level);
                pair.setLeft(startLog);
                return pair;
            });

            if (!PRIVATE_LOG_LEVEL_PLAYERS.containsKey(player)) {
                GETTING_GLOBAL_LOG_PLAYERS.remove(player);
                shouldAddMap.put(player, value);
            }
        });

        PRIVATE_LOG_LEVEL_PLAYERS.putAll(shouldAddMap);
    }

    private void setPrivate2Global(Collection<ServerPlayerEntity> players) {
        players.forEach(player -> {
            if (PRIVATE_LOG_LEVEL_PLAYERS.get(player).getLeft())
                GETTING_GLOBAL_LOG_PLAYERS.add(player);

            PRIVATE_LOG_LEVEL_PLAYERS.remove(player);
        });
    }

    private ArrayList<String> removeFromPrivate(Collection<ServerPlayerEntity> players) {
        ArrayList<String> list = new ArrayList<>();

        PRIVATE_LOG_LEVEL_PLAYERS.forEach((player, pair) -> {
            if (players.contains(player)) {
                PRIVATE_LOG_LEVEL_PLAYERS.remove(player);
                list.add(player.getNameForScoreboard());
            }
        });

        return list;
    }
}