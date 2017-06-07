package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.error.CardListPatternException;

public class A4 extends A1 {
	@Override
	public int compareTo(CardList arg0) {
		if (arg0.getClass() == KQ.class)
			return -1;

		if (getClass() != arg0.getClass())
			return 1;

		return getNum() - ((A4) arg0).getNum();
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() != 4)
			throw new CardListPatternException();
		Set<Integer> set = cardSort.getCardSort().get(3);
		if (set.size() == 0)
			throw new CardListPatternException();

		int num = set.iterator().next();

		A4 a = new A4();
		a.setNum(num);
		return a;
	}

	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList,
			List<Integer> arr) {
		if (arr.size() < 4 || cardSort.getCardSort().get(3).size() < 1)
			return;

		Set<Integer> set = cardSort.getCardSort().get(3);
		if (lastCardList == null) {
			// 主动出牌
			for (int pai : set) {
				List<Integer> list = new ArrayList<>(3);
				for (int i = 0; i < 4; i++)
					list.add(pai);

				recommandList.add(list);
			}
		} else {
			// 被动出牌
			if (lastCardList.getClass() != KQ.class) {
				List<Integer> temp = new ArrayList<>(set);
				for (int pai = lastCardList.getClass() == A4.class ? ((A4) lastCardList).getNum() + 1
						: temp.get(0); pai <= temp.get(temp.size() - 1); pai++) {
					if (!set.contains(pai))
						continue;

					List<Integer> list = new ArrayList<>(3);
					for (int i = 0; i < 4; i++)
						list.add(pai);

					recommandList.add(list);
				}
			}
		}
	}

}
