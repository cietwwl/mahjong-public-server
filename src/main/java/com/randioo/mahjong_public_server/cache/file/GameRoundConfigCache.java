package com.randioo.mahjong_public_server.cache.file;

import java.util.HashMap;
import java.util.Map;

import com.randioo.mahjong_public_server.entity.file.GameRoundConfig;

/**
 * 游戏回合配置表
 * 
 * @author wcy 2017年8月18日
 *
 */
public class GameRoundConfigCache {

    private static Map<Integer, GameRoundConfig> map = new HashMap<>();
    private static Map<Integer, GameRoundConfig> roundMap = new HashMap<>();

    public static void putConfig(GameRoundConfig config) {
        map.put(config.id, config);
        roundMap.put(config.round, config);
    }

    public static GameRoundConfig getGameRoundById(int id) {
        return map.get(id);
    }

    public static GameRoundConfig getGameRoundByRoundCount(int round) {
        return roundMap.get(round);
    }

}
