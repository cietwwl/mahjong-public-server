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
public class Kan implements CardList {
	public int card;

	@Override
	public void check(List<CardList> cardLists, CardSort cardSort, int card) {
		Set<Integer> set = cardSort.getList().get(2);
		if (set.contains(card)) {
			Kan kan = new Kan();
			kan.card = card;
			cardLists.add(kan);
		}
	}
}
