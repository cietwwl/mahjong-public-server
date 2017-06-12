package com.randioo.mahjong_public_server.module.fight.service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface FightService extends ObserveBaseServiceInterface {
	public void readyGame(Role role);

	GeneratedMessage exitGame(Role role);

	GeneratedMessage agreeExit(Role role, boolean agree);

	/**
	 * 真实玩家出牌
	 * 
	 * @param role
	 * @param paiList
	 */
	void sendCard(Role role, int card);

	/**
	 * 分牌
	 * 
	 * @param gameId
	 * @author wcy 2017年5月31日
	 */
	void dispatchCard(int gameId);

	/**
	 * 游戏开始
	 * 
	 * @param gameId
	 */
	void gameStart(int gameId);

	/**
	 * 摸牌
	 * 
	 * @param gameRoleId
	 * @param gameId
	 */
	void touchCard(String gameRoleId, int gameId);

	void peng(Role role, int card);

	void gang(Role role, int card);

	void hu(Role role);

}
