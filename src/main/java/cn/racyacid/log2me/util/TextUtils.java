package cn.racyacid.log2me.util;

import cn.racyacid.log2me.enums.LogLevelColors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextUtils {
    public static List<String> getNames(Collection<? extends Entity> entities) {
        if (entities instanceof Map) throw new IllegalArgumentException("Not supported Map yet!");
        ArrayList<String> names = new ArrayList<>();
        entities.forEach(entity -> names.add(entity.getNameForScoreboard()));
        return names;
    }

    public static String getStringNames(Collection<? extends Entity> entities) {
        return StringUtils.join(getNames(entities));
    }

    public static Text getStyledLogLevelByMap(Map<ServerPlayerEntity, Pair<Boolean, Level>> map, boolean logging) {
        Map<ServerPlayerEntity, Level> hashMap = new HashMap<>();

        map.forEach((player, pair) -> {
            if (logging == pair.getLeft()) hashMap.put(player, pair.getRight());
        });

        return getStyledLogLevelByMap(hashMap);
    }

    public static Text getStyledLogLevelByMap(Map<? extends PlayerEntity, Level> map) {
        ArrayList<Text> siblings = new ArrayList<>();
        siblings.add(Text.of("\n"));

        map.forEach((player, level) -> {
            siblings.add(Text.literal(player.getNameForScoreboard() + " -> ").append(getStyledLogLevel(level)));
            siblings.add(Text.of("\n"));
        });

        siblings.removeLast();

        MutableText text = Text.empty();
        siblings.forEach(text::append);

        return text;
    }

    public static Text getStyledLogLevel(@NotNull Level level) {
        return Text.literal(level.toString()).setStyle(Style.EMPTY.withItalic(true).withColor(LogLevelColors.getLogLevelColor(level)));
    }
}