package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.error.CardListPatternException;
import com.randioo.mahjong_public_server.error.CardTypeComparableException;
import com.randioo.mahjong_public_server.util.CardTools;

public class A3 extends A1 {
	@Override
	public int compareTo(CardList arg0) {
		if (arg0.getClass() == A4.class || arg0.getClass() == KQ.class)
			return -1;

		if (getClass() != arg0.getClass())
			throw new CardTypeComparableException();

		return getNum() - ((A3) arg0).getNum();
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() != 3)
			throw new CardListPatternException();
		Set<Integer> set = cardSort.getCardSort().get(2);
		if (set.size() == 0)
			throw new CardListPatternException();

		int num = set.iterator().next();

		A3 a = new A3();
		a.setNum(num);
		return a;
	}

	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList,
			List<Integer> arr) {
		if (arr.size() < 3 || cardSort.getCardSort().get(2).size() < 1)
			return;

		CardTools.recommandNumCommonTemplate(recommandList, cardSort, lastCardList, 2, this.getClass());

	}

}
