package com.randioo.mahjong_public_server.module.fight.service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.entity.po.CallCardList;
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
	void sendCard(Role role, int card, boolean isTouchCard);

	/**
	 * 分牌
	 * 
	 * @param gameId
	 * @author wcy 2017年5月31日
	 */
	void dispatchCard(Game game);

	/**
	 * 游戏开始
	 * 
	 * @param gameId
	 */
	void gameStart(Game game);

	/**
	 * 摸牌
	 * 
	 * @param gameRoleId
	 * @param gameId
	 */
	void touchCard(Game game);

	void peng(Role role, int gameSendCount, int callCardListId);

	void gang(Role role, int gameSendCount, int callCardListId);

	void hu(Role role, int gameSendCount, int callCardListId);

	void guo(Role role, int gameSendCount);

	/**
	 * 获得之前一个人的卡组
	 * 
	 * @param gameId
	 * @author wcy 2017年6月17日
	 */
	CallCardList getPreviousCallCardList(Game game);

	/**
	 * 获得游戏
	 * 
	 * @param gameId
	 * @return
	 */
	Game getGameById(int gameId);

}
