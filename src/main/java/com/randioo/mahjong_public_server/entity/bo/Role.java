package com.randioo.mahjong_public_server.entity.bo;

import com.randioo.mahjong_public_server.entity.po.RoleRaceInfo;
import com.randioo.randioo_server_base.entity.DefaultRole;

public class Role extends DefaultRole {

	private int money;
	private int gameId;
	private int sex;
	private int volume = 0;
	private int musicVolume = 0;
	private String moneyExchangeTimeStr;
	private String headImgUrl;
	private int randiooMoney;
	private int moneyExchangeNum;
	/** 玩家比赛信息 */
	private RoleRaceInfo roleRaceInfo;
	/** 比赛底分 */
	private int raceScore;
	/** 积分 */
	private int point;

	public int getRandiooMoney() {
		return randiooMoney;
	}

	public void setRandiooMoney(int randiooMoney) {
		this.randiooMoney = randiooMoney;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public int getMusicVolume() {
		return musicVolume;
	}

	public void setMusicVolume(int musicVolume) {
		this.musicVolume = musicVolume;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		setChange(true);
		this.money = money;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getGameId() {
		return gameId;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getMoneyExchangeTimeStr() {
		return moneyExchangeTimeStr;
	}

	public void setMoneyExchangeTimeStr(String moneyExchangeTimeStr) {
		this.moneyExchangeTimeStr = moneyExchangeTimeStr;
	}

	public int getMoneyExchangeNum() {
		return moneyExchangeNum;
	}

	public void setMoneyExchangeNum(int moneyExchangeNum) {
		this.moneyExchangeNum = moneyExchangeNum;
	}

	public int getRaceScore() {
		return raceScore;
	}

	public void setRaceScore(int raceScore) {
		this.raceScore = raceScore;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public RoleRaceInfo getRoleRaceInfo() {
		return roleRaceInfo;
	}

	public void setRoleRaceInfo(RoleRaceInfo roleRaceInfo) {
		this.roleRaceInfo = roleRaceInfo;
	}

}
