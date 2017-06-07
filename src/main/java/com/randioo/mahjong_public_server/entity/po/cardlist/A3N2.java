package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.error.CardListPatternException;
import com.randioo.mahjong_public_server.error.CardTypeComparableException;

public class A3N2 extends A3N1 {

	@Override
	public int compareTo(CardList o) {
		if (o.getClass() == A4.class || o.getClass() == KQ.class)
			return -1;
		if (o.getClass() != getClass())
			throw new CardTypeComparableException();

		return getNum() - ((A3N2) o).getNum();
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() != 5)
			throw new CardListPatternException();
		Set<Integer> set = cardSort.getCardSort().get(1);
		Set<Integer> set2 = cardSort.getCardSort().get(2);
		if (set.size() == 2 && set2.size() == 1) {
			A3N2 a = new A3N2();
			a.setNum(set2.iterator().next());
			for (int i : set)
				if (i != a.getNum())
					a.setAddNum(i);

			return a;
		}

		throw new CardListPatternException();
	}

	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList,
			List<Integer> arr) {
		// TODO Auto-generated method stub
		super.recommand(recommandList, cardSort, lastCardList, arr);
	}

}
