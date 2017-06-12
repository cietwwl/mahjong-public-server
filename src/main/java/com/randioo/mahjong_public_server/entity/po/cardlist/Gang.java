package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;

public class Gang implements CardList {
	public int card;

	@Override
	public void check(List<CardList> cardLists, CardSort cardSort, int card) {
		Set<Integer> set = cardSort.getList().get(2);
		if (set.contains(card)) {
			Gang gang = new Gang();
			gang.card = card;
			cardLists.add(gang);
		}
	}
}
