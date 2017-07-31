package com.randioo.mahjong_public_server.cache.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.module.fight.component.cardlist.CardList;
import com.randioo.randioo_server_base.template.Function;

public class GameCache {
	private static Map<Integer, Game> gameMap = new LinkedHashMap<>();
	private static Map<String, Integer> gameLockMap = new LinkedHashMap<>();
	private static Map<Class<? extends CardList>, CardList> cardLists = new HashMap<>();

	private static List<Class<? extends CardList>> checkCardListSequence = new ArrayList<>();
	private static List<Class<? extends CardList>> checkSelfCardList = new ArrayList<>();
	private static List<Class<? extends CardList>> checkGangCardList = new ArrayList<>();
	private static Map<Class<? extends CardList>, Function> parseCardListToProtoFunctionMap = new HashMap<>();
	private static Map<Class<? extends CardList>, Function> addProtoFunctionMap = new HashMap<>();

	private static Set<Integer> baiDaCardNumSet = new HashSet<>();

	public static Map<Integer, Game> getGameMap() {
		return gameMap;
	}

	public static Map<String, Integer> getGameLockStringMap() {
		return gameLockMap;
	}

	public static Map<Class<? extends CardList>, CardList> getCardLists() {
		return cardLists;
	}

	public static List<Class<? extends CardList>> getCheckCardListSequence() {
		return checkCardListSequence;
	}

	public static Map<Class<? extends CardList>, Function> getParseCardListToProtoFunctionMap() {
		return parseCardListToProtoFunctionMap;
	}

	public static Map<Class<? extends CardList>, Function> getNoticeChooseCardListFunctionMap() {
		return addProtoFunctionMap;
	}

	public static List<Class<? extends CardList>> getCheckSelfCardList() {
		return checkSelfCardList;
	}

	public static List<Class<? extends CardList>> getCheckGangCardList() {
		return checkGangCardList;
	}

	public static Set<Integer> getBaiDaCardNumSet() {
		return baiDaCardNumSet;
	}
}
