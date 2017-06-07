package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.error.CardListPatternException;
import com.randioo.mahjong_public_server.error.CardTypeComparableException;

public class A3N1 extends A1 {

	private int addNum;

	public void setAddNum(int addNum) {
		this.addNum = addNum;
	}

	public int getAddNum() {
		return addNum;
	}

	@Override
	public int compareTo(CardList o) {
		if (o.getClass() == A4.class || o.getClass() == KQ.class)
			return -1;
		if (o.getClass() != getClass())
			throw new CardTypeComparableException();

		return getNum() - ((A3N1) o).getNum();
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() != 4)
			throw new CardListPatternException();

		Set<Integer> set0 = cardSort.getCardSort().get(0);
		Set<Integer> set2 = cardSort.getCardSort().get(2);

		if (set0.size() == 2 && set2.size() == 1) {
			int num = set2.iterator().next();
			int addNum = addNum(cardSort, num);
			A3N1 a = new A3N1();
			a.setNum(num);
			a.setAddNum(addNum);
		}

		throw new CardListPatternException();
	}

	private int addNum(CardSort cardSort, int value) {
		for (int n : cardSort.getCardSort().get(0)) {
			if (n != value) {
				return n;
			}
		}
		return 0;
	}

	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList,
			List<Integer> arr) {
		// TODO Auto-generated method stub
		super.recommand(recommandList, cardSort, lastCardList, arr);
	}


}
