package com.randioo.mahjong_public_server.entity.bo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	// 最大玩家数量
	private int maxRoleCount;
	// 游戏开始
	private GameState gameState;
	// 游戏类型
	private GameType gameType;
	// 游戏配置
	private GameConfig gameConfig;
	// 地主牌
	private List<Integer> landlordCards = new ArrayList<>();
	// 在线玩家数量
	private int onlineRoleCount;
	// 玩家id列表，用于换人
	private List<String> roleIdList = new ArrayList<>();
	// 当前玩家id
	private int currentRoleIdIndex;
	// 出牌的类型
	private CardList lastCardList;
	// 不要
	private int passCount;
	// 地主的玩家id
	private String landlordGameRoleId;
	// 游戏倍数
	private int multiple;
	// 叫地主的分数
	private int callLandlordScore;
	// 叫分的次数
	private int callLandlordCount;
	// 出牌计数
	private int sendCardCount;
	// 出牌的时间戳
	private int sendCardTime;
	
	private boolean mingPaiState;

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

	public int getMaxRoleCount() {
		return maxRoleCount;
	}

	public void setMaxRoleCount(int maxRoleCount) {
		this.maxRoleCount = maxRoleCount;
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

	public List<Integer> getLandlordCards() {
		return landlordCards;
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

	public CardList getLastCardList() {
		return lastCardList;
	}

	public void setLastCardList(CardList lastCardList) {
		this.lastCardList = lastCardList;
	}

	public int getPassCount() {
		return passCount;
	}

	public void setPassCount(int passCount) {
		this.passCount = passCount;
	}

	public void setLandlordGameRoleId(String landlordGameRoleId) {
		this.landlordGameRoleId = landlordGameRoleId;
	}

	public String getLandlordGameRoleId() {
		return landlordGameRoleId;
	}

	public int getMultiple() {
		return multiple;
	}

	public void setMultiple(int multiple) {
		this.multiple = multiple;
	}

	public int getCallLandlordScore() {
		return callLandlordScore;
	}

	public void setCallLandlordScore(int callLandlordScore) {
		this.callLandlordScore = callLandlordScore;
	}

	public int getCallLandlordCount() {
		return callLandlordCount;
	}

	public void setCallLandlordCount(int callLandlordCount) {
		this.callLandlordCount = callLandlordCount;
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

	public boolean isMingPaiState() {
		return mingPaiState;
	}

	public void setMingPaiState(boolean mingPaiState) {
		this.mingPaiState = mingPaiState;
	}
	

}
