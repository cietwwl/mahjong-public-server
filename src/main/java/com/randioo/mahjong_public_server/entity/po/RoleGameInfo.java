package com.randioo.mahjong_public_server.entity.po;

import java.util.ArrayList;
import java.util.List;

import com.randioo.mahjong_public_server.entity.po.cardlist.CardList;

public class RoleGameInfo {
	/** 游戏中的玩家id */
	public String gameRoleId;
	/** 全局玩家id */
	public int roleId;
	/** 是否准备完成 */
	public boolean ready;
	public Boolean agreeLeave;
	/** 手上看不到的牌 */
	public List<Integer> cards = new ArrayList<>();
	/** 自动出牌标记 */
	public int auto;
	/** 新拿的牌 */
	public int newCard;
	/** 已经碰过或杠过的牌 */
	public List<CardList> showCardLists = new ArrayList<>();

	@Override
	public String toString() {
		return "[gameRoleId=" + gameRoleId + "|roleId=" + roleId + "|ready=" + ready + "|cards=" + cards
				+ "|newCard=" + newCard + " ]";
	}
}
