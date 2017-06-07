package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.error.CardListPatternException;

public class KQ extends CardList {

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() != 2)
			throw new CardListPatternException();
		Set<Integer> set = cardSort.getCardSort().get(0);
		if (set.size() == 2 && set.contains(14) && set.contains(15))
			return new KQ();

		throw new CardListPatternException();
	}

	@Override
	public int compareTo(CardList o) {
		return 1;
	}

	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList,
			List<Integer> arr) {
		
	}


}
