package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;

public class Gang extends AbstractCardList {
	public int card;
	public boolean visible;
	public int pengSeat;

	@Override
	public void check(List<CardList> cardLists, CardSort cardSort, int card) {
		Set<Integer> set = cardSort.getList().get(2);
		if (set.contains(card)) {
			Gang gang = new Gang();
			gang.card = card;
			cardLists.add(gang);
		}
	}

	@Override
	public List<Integer> getCards() {
		List<Integer> list = new ArrayList<>(4);
		for (int i = 0; i < 4; i++)
			list.add(card);
		return list;
	}
}
