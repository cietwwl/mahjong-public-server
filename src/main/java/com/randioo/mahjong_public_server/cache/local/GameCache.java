package com.randioo.mahjong_public_server.cache.local;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.po.cardlist.CardList;
import com.randioo.mahjong_public_server.entity.po.cardlist.Gang;
import com.randioo.mahjong_public_server.entity.po.cardlist.Hu;
import com.randioo.mahjong_public_server.entity.po.cardlist.Kan;
import com.randioo.mahjong_public_server.entity.po.cardlist.Shun;

public class GameCache {
	private static Map<Integer, Game> gameMap = new LinkedHashMap<>();
	private static Map<String, Integer> gameLockMap = new LinkedHashMap<>();
	private static Map<Class<? extends CardList>, CardList> cardLists = new HashMap<>();
	private static Gang gang = null;
	private static Kan kan = null;
	private static Shun shun = null;
	private static Hu hu = null;

	public static Map<Integer, Game> getGameMap() {
		return gameMap;
	}

	public static Map<String, Integer> getGameLockStringMap() {
		return gameLockMap;
	}

	public static Map<Class<? extends CardList>, CardList> getCardLists() {
		return cardLists;
	}

	public static void setGang(Gang gang) {
		GameCache.gang = gang;
	}

	public static Gang getGang() {
		return gang;
	}

	public static Kan getKan() {
		return kan;
	}

	public static void setKan(Kan kan) {
		GameCache.kan = kan;
	}

	public static Shun getShun() {
		return shun;
	}

	public static void setShun(Shun shun) {
		GameCache.shun = shun;
	}

	public static Hu getHu() {
		return hu;
	}

	public static void setHu(Hu hu) {
		GameCache.hu = hu;
	}

}
