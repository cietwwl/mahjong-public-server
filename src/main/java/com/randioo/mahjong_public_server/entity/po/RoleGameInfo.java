package com.randioo.mahjong_public_server.entity.po;

import java.util.ArrayList;
import java.util.List;

import com.randioo.mahjong_public_server.entity.po.cardlist.CardList;
import com.randioo.mahjong_public_server.entity.po.cardlist.Gang;
import com.randioo.mahjong_public_server.protocol.Entity.RoundCardsData;

public class RoleGameInfo {
	/** 游戏中的玩家id */
	public String gameRoleId;
	/** 全局玩家id */
	public int roleId;
	/** 手上看不到的牌 */
	public List<Integer> cards = new ArrayList<>();
	/** 已经碰过或杠过的牌 */
	public List<CardList> showCardLists = new ArrayList<>();
	/** 新拿的牌 */
	public int newCard;
	/** 是否准备完成 */
	public boolean ready;
	/** 同意离开 */
	public Boolean agreeLeave;
	/** 自动出牌标记 */
	public int auto;
	/** 杠标记 */
	public boolean isGang;
	/** 分数 */
	public int score;
	/** 胡牌记录 */
	public RoundCardsData roundCardsData;
	/** 是否要摸牌 */
	public Gang qiangGang = null;
	/** 听的牌 */
	public List<Integer> tingCards = new ArrayList<>();

	@Override
	public String toString() {
		String n = System.getProperty("line.separator");
		String t = "\t";
		StringBuilder sb = new StringBuilder();
		sb.append("GameRoleInfo:[").append(n);
		sb.append(t).append("gameRoleId=>").append(gameRoleId).append(n);
		sb.append(t).append("roleId=>").append(roleId).append(n);
		sb.append(t).append("ready=>").append(ready).append(n);
		sb.append(t).append("cards=>").append(cards).append(n);
		sb.append(t).append("newCard=>").append(newCard).append(n);
		sb.append(t).append("showCardLists=>").append(showCardLists).append(n);
		sb.append(t).append("score=>").append(score).append(n);
		sb.append(t).append("qiangGang=>").append(qiangGang).append(n);
		sb.append(t).append("]");

		return sb.toString();
	}
}
