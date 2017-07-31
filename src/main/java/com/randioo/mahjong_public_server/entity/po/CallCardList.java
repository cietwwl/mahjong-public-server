package com.randioo.mahjong_public_server.entity.po;

import com.randioo.mahjong_public_server.module.fight.component.cardlist.CardList;

public class CallCardList {
	public int cardListId;
	public CardList cardList;
	/** 拥有卡组的人 */
	public int masterSeat;
	public boolean call;

	@Override
	public String toString() {
		String n = System.getProperty("line.separator");
		String t = "\t";
		StringBuilder sb = new StringBuilder();
		sb.append("CallCardList:[").append(n);
		sb.append(t).append("cardListId:").append(cardListId).append(n);
		sb.append(t).append("cardList:").append(cardList).append(n);
		sb.append(t).append("masterSeat:").append(masterSeat).append(n);
		sb.append(t).append("call:").append(call).append(n);
		sb.append(t).append("]").append(n);
		return sb.toString();
	}
}
