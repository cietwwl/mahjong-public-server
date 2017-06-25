package com.randioo.mahjong_public_server.entity.po;

import java.util.ArrayList;
import java.util.List;

public class Race {
	/** 比赛id */
	private int raceId;
	// 游戏id
	private int gameId;
	// 比赛名称
	private String raceName;
	// 结束时间
	private String endTime;
	// 玩家队列
	private List<Integer> roleIdQueue = new ArrayList<>();

	private MahjongRaceConfigure config;

	public List<Integer> getRoleIdQueue() {
		return roleIdQueue;
	}

	public String getRaceName() {
		return raceName;
	}

	public void setRaceName(String raceName) {
		this.raceName = raceName;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getRaceId() {
		return raceId;
	}

	public void setRaceId(int raceId) {
		this.raceId = raceId;
	}

	public MahjongRaceConfigure getConfig() {
		return config;
	}

	public void setConfig(MahjongRaceConfigure config) {
		this.config = config;
	}
}
