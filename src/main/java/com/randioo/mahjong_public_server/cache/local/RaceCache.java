package com.randioo.mahjong_public_server.cache.local;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.randioo.mahjong_public_server.entity.po.Race;

public class RaceCache {
	/**
	 * 比赛id,比赛
	 */
	private static Map<Integer, Race> raceMap = new ConcurrentHashMap<>();

	public static Map<Integer, Race> getRaceMap() {
		return raceMap;
	}

	private static Map<Integer, Race> gameRaceMap = new ConcurrentHashMap<>();

	public static Map<Integer, Race> getGameRaceMap() {
		return gameRaceMap;
	}
}
