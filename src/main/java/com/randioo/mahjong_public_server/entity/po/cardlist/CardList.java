package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.List;

import com.randioo.mahjong_public_server.entity.po.CardSort;

public interface CardList {
	public void check(List<CardList> cardLists, CardSort cardSort, int card);
}
