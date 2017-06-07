package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.error.CardListPatternException;
import com.randioo.mahjong_public_server.error.CardTypeComparableException;

public class A4B2C2 extends A4 {

	private List<Integer> addNumList = new ArrayList<>();

	public List<Integer> getAddNumList() {
		return addNumList;
	}

	public void setAddNumList(List<Integer> addNumList) {
		this.addNumList = addNumList;
	}



	@Override
	public int compareTo(CardList arg0) {
		if (arg0.getClass() == A4.class || arg0.getClass() == KQ.class)
			return -1;

		if (getClass() != arg0.getClass())
			throw new CardTypeComparableException();

		A4B2C2 a4b2c2 = (A4B2C2) arg0;

		return getNum() - a4b2c2.getNum();
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() != 8)
			throw new CardListPatternException();
		
		Set<Integer> set1 = cardSort.getCardSort().get(1);
		Set<Integer> set3 = cardSort.getCardSort().get(3);
		if (set3.size() != 1)
			throw new CardListPatternException();
		// 如果是带对子

		if (set1.size() != 3)
			throw new CardListPatternException();
		A4B2C2 a4b2c2 = new A4B2C2();
		a4b2c2.setNum(set3.iterator().next());
		for (int i : set1) {
			if (i != a4b2c2.getNum()) {
				a4b2c2.getAddNumList().add(i);
			}
		}

		return a4b2c2;
	}
	
	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList, List<Integer> arr) {
		/*Set<Integer> set3 = cardSort.getCardSort().get(3);
		Set<Integer> set0 = cardSort.getCardSort().get(0);
		if (arr.size() < 6 || set3.size() < 1)
			return;

		if (lastCardList == null) {
			// 主动出牌
			if
			for (int i : set) {
				List<Integer> list = new ArrayList<>(4);
				list.add(i);
				list.add(i);
				list.add(i);
				list.add(i);
				recommandList.add(list);
			}
		} else {
			// 被动出牌
			if (lastCardList.getClass() != KQ.class) {
				if(lastCardList.getClass() != A4.class){
					for (int i : set) {
						List<Integer> list = new ArrayList<>(4);
						list.add(i);
						list.add(i);
						list.add(i);
						list.add(i);
						recommandList.add(list);
					}
				}
				A4 a2 = (A4) lastCardList;
				int num = a2.getNum();
				for (int i = num + 1; i < 0xE; i++) {
					if (set.contains(i)) {
						List<Integer> list = new ArrayList<>(4);
						list.add(i);
						list.add(i);
						list.add(i);
						list.add(i);
						recommandList.add(list);
					}
				}
			}
		}*/
	}

}
