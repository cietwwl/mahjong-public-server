package com.randioo.mahjong_public_server.entity.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.randioo.mahjong_public_server.entity.po.CallCardListsAction;
import com.randioo.mahjong_public_server.entity.po.RoleGameInfo;
import com.randioo.mahjong_public_server.entity.po.cardlist.CardList;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfig;
import com.randioo.mahjong_public_server.protocol.Entity.GameState;
import com.randioo.mahjong_public_server.protocol.Entity.GameType;

public class Game {
	private int gameId;
	// 玩家id集合
	private Map<String, RoleGameInfo> roleIdMap = new LinkedHashMap<>();
	// 房主id
	private int masterRoleId;
	// 房间锁
	private String lockString;
	// 游戏开始
	private GameState gameState;
	// 游戏类型
	private GameType gameType;
	// 游戏配置
	private GameConfig gameConfig;
	// 在线玩家数量
	private int onlineRoleCount;
	// 玩家id列表，用于换人
	private List<String> roleIdList = new ArrayList<>();
	// 当前玩家id
	private int currentRoleIdIndex;
	// 游戏倍数
	private int multiple;
	// 出牌计数
	private int sendCardCount;
	// 出牌的时间戳
	private int sendCardTime;
	// 庄家的玩家id
	private String zhuangGameRoleId;
	// 剩余的牌
	private List<Integer> remainCards = new ArrayList<>();
	// 桌上的牌<索引id,出牌的列表>
	private Map<Integer, List<Integer>> desktopCardMap = new HashMap<>();
	// 每个人每次叫牌的临时存储
	private Map<Integer, CallCardListsAction> tempCardListMap = new HashMap<>();
	// 出牌放在桌上的表
	private Map<Integer, List<Integer>> sendDesktopCardMap = new HashMap<>();

	public int getOnlineRoleCount() {
		return onlineRoleCount;
	}

	public void setOnlineRoleCount(int onlineRoleCount) {
		this.onlineRoleCount = onlineRoleCount;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getGameId() {
		return gameId;
	}

	public Map<String, RoleGameInfo> getRoleIdMap() {
		return roleIdMap;
	}

	public int getMasterRoleId() {
		return masterRoleId;
	}

	public void setMasterRoleId(int masterRoleId) {
		this.masterRoleId = masterRoleId;
	}

	public String getLockString() {
		return lockString;
	}

	public void setLockString(String lockString) {
		this.lockString = lockString;
	}

	public GameType getGameType() {
		return gameType;
	}

	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public GameConfig getGameConfig() {
		return gameConfig;
	}

	public void setGameConfig(GameConfig gameConfig) {
		this.gameConfig = gameConfig;
	}

	public List<String> getRoleIdList() {
		return roleIdList;
	}

	public int getCurrentRoleIdIndex() {
		return currentRoleIdIndex;
	}

	public void setCurrentRoleIdIndex(int currentRoleIdIndex) {
		this.currentRoleIdIndex = currentRoleIdIndex;
	}

	public int getMultiple() {
		return multiple;
	}

	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}

	public int getSendCardCount() {
		return sendCardCount;
	}

	public void setSendCardCount(int sendCardCount) {
		this.sendCardCount = sendCardCount;
	}

	public int getSendCardTime() {
		return sendCardTime;
	}

	public void setSendCardTime(int sendCardTime) {
		this.sendCardTime = sendCardTime;
	}

	public String getZhuangGameRoleId() {
		return zhuangGameRoleId;
	}

	public void setZhuangGameRoleId(String zhuangGameRoleId) {
		this.zhuangGameRoleId = zhuangGameRoleId;
	}

	/**
	 * 获得剩余牌
	 * 
	 * @return
	 */
	public List<Integer> getRemainCards() {
		return remainCards;
	}

	public Map<Integer, List<Integer>> getDesktopCardMap() {
		return desktopCardMap;
	}

	public Map<Integer, List<Integer>> getSendDesktopCardMap() {
		return sendDesktopCardMap;
	}

	public Map<Integer, CallCardListsAction> getTempCardListMap() {
		return tempCardListMap;
	}
}
