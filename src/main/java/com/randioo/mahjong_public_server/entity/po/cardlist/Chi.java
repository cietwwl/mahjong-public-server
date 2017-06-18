package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;

public class Chi extends AbstractCardList {
	public int card;

	@Override
	public void check(List<CardList> cardLists, CardSort cardSort, int card) {
		Set<Integer> set = cardSort.getList().get(0);

	}

	@Override
	public List<Integer> getCards() {
		List<Integer> list = new ArrayList<>(3);
		for (int i = 0; i < 3; i++)
			list.add(card + i);
		return list;
	}
	
}
