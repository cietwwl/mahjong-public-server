package com.randioo.mahjong_public_server.entity.po;

/**
 * 
 * 排名
 * 
 */
public class Rank {

	private String account ;
	private int score ;
	private int isNPC ;
	public String getAccount() {
		return account;
	}
	public int getIsNPC() {
		return isNPC;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public void setIsNPC(int isNPC) {
		this.isNPC = isNPC;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
}
