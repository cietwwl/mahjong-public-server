package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;

public class Shun implements CardList {
	public int card;

	@Override
	public void check(List<CardList> cardLists, CardSort cardSort, int card) {
		Set<Integer> set = cardSort.getList().get(0);
		
	}
}
