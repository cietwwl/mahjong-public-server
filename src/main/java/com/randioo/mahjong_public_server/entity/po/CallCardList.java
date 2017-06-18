package com.randioo.mahjong_public_server.entity.po;

import com.randioo.mahjong_public_server.entity.po.cardlist.CardList;

public class CallCardList {
	public int cardListId;
	public CardList cardList;
	/** 拥有卡组的人 */
	public int masterSeat;
	public boolean call;
}
