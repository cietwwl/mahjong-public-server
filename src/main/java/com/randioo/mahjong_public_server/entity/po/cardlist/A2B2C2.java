package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.error.CardListPatternException;
import com.randioo.mahjong_public_server.error.CardTypeComparableException;
import com.randioo.mahjong_public_server.util.CardTools;

public class A2B2C2 extends ABCDE {
	@Override
	public int compareTo(CardList arg0) {
		if (arg0.getClass() == A4.class || arg0.getClass() == KQ.class)
			return -1;

		if (getClass() != arg0.getClass())
			throw new CardTypeComparableException();

		A2B2C2 a2b2c2 = (A2B2C2) arg0;
		if (getLength() != a2b2c2.getLength())
			throw new CardTypeComparableException();

		return getNum() - ((A2B2C2) arg0).getNum();
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() < 6 && (arr.size() - 6) % 2 != 0)
			throw new CardListPatternException();
		Set<Integer> set0 = cardSort.getCardSort().get(0);
		Set<Integer> set1 = cardSort.getCardSort().get(1);
		Set<Integer> set2 = cardSort.getCardSort().get(2);
		if (set2.size() > 0 || set0.size() != set1.size() || set0.contains(13) || set0.contains(14)
				|| set0.contains(15))
			throw new CardListPatternException();

		int tmp = 0;
		int first = 0;
		for (int value : set1) {
			if (tmp == 0) {
				first = value;
				tmp = value;
			} else if (value != (tmp + 1))
				throw new CardListPatternException();
			else
				tmp = value;
		}
		A2B2C2 a = new A2B2C2();
		a.setNum(first);
		a.setLength(set1.size());
		return a;

	}

	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList, List<Integer> arr) {
		if (arr.size() < 6 || cardSort.getCardSort().get(1).size() < 3)
			return;

		if (lastCardList != null) {
			CardTools.recommandStartNumAndLenCommonTemplate(recommandList, cardSort, lastCardList, arr, 1, 2);
		}
	}

}
