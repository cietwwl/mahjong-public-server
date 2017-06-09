package com.randioo.mahjong_public_server.cache.local;

import java.util.LinkedHashMap;
import java.util.Map;

import com.randioo.mahjong_public_server.entity.bo.Game;

public class GameCache {
	private static Map<Integer, Game> gameMap = new LinkedHashMap<>();
	private static Map<String, Integer> gameLockMap = new LinkedHashMap<>();
	

	public static Map<Integer, Game> getGameMap() {
		return gameMap;
	}

	public static Map<String, Integer> getGameLockStringMap() {
		return gameLockMap;
	}

	

}
