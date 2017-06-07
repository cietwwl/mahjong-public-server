package com.randioo.mahjong_public_server.cache.local;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.po.cardlist.CardList;

public class GameCache {
	private static Map<Integer, Game> gameMap = new LinkedHashMap<>();
	private static Map<String, Integer> gameLockMap = new LinkedHashMap<>();
	private static Map<Class<? extends CardList>, CardList> cardLists = new LinkedHashMap<>();
	private static Map<Class<? extends CardList>, CardList> recommendCardLists = new LinkedHashMap<>();

	public static Map<Integer, Game> getGameMap() {
		return gameMap;
	}

	public static Map<String, Integer> getGameLockStringMap() {
		return gameLockMap;
	}

	public static Map<Class<? extends CardList>, CardList> getCardLists() {
		return cardLists;
	}

	public static List<CardList> sendCardSeqCheckerList = new ArrayList<>();

	public static List<CardList> getSendCardSeqCheckerList() {
		return sendCardSeqCheckerList;
	}

	public static Map<Class<? extends CardList>, CardList> getRecommendCardLists() {
		return recommendCardLists;
	}
	

}
