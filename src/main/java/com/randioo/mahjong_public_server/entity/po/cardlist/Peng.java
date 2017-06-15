package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;

/**
 * 三个相同
 * 
 * @author wcy 2017年6月12日
 *
 */
public class Peng implements CardList {
	public int card;
	public int seat;

	@Override
	public void check(List<CardList> cardLists, CardSort cardSort, int card) {
		Set<Integer> set = cardSort.getList().get(2);
		if (set.contains(card)) {
			Peng peng = new Peng();
			peng.card = card;
			cardLists.add(peng);
		}
	}

}
