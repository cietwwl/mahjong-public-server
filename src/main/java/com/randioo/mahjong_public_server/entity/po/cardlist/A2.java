package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.error.CardListPatternException;
import com.randioo.mahjong_public_server.error.CardTypeComparableException;
import com.randioo.mahjong_public_server.util.CardTools;

public class A2 extends A1 {
	@Override
	public int compareTo(CardList arg0) {
		if (arg0.getClass() == A4.class || arg0.getClass() == KQ.class)
			return -1;

		if (getClass() != arg0.getClass())
			throw new CardTypeComparableException();

		return getNum() - ((A2) arg0).getNum();
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() != 2)
			throw new CardListPatternException();
		Set<Integer> set = cardSort.getCardSort().get(1);
		if (set.size() == 0)
			throw new CardListPatternException();

		int num = set.iterator().next();

		A2 a = new A2();
		a.setNum(num);
		return a;

	}

	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList, List<Integer> arr) {

		if (arr.size() < 2 || cardSort.getCardSort().get(1).size() < 1)
			return;

		CardTools.recommandNumCommonTemplate(recommandList, cardSort, lastCardList, 1, this.getClass());
	}

	public static void main(String[] args) {
		A2 a1 = new A2();
		List<List<Integer>> recommandList = new ArrayList<>();
		List<Integer> list = new ArrayList<>();
		list.add(0x11);
		list.add(0x11);
		list.add(0x13);
		list.add(0x13);
		list.add(0x1B);
		list.add(0x1B);
		list.add(0x1D);
		list.add(0x1D);
		list.add(0x16);
		list.add(0x16);
		list.add(0x0E);
		list.add(0x0F);

		CardSort cardSort = new CardSort();
		CardTools.fillCardSort(cardSort, list);

		A2 lastCardList = new A2();
		lastCardList.setNum(3);
		a1.recommand(recommandList, cardSort, lastCardList, list);

		System.out.println(recommandList);
	}
}
