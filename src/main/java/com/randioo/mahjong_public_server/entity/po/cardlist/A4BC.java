package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.error.CardListPatternException;
import com.randioo.mahjong_public_server.error.CardTypeComparableException;
import com.randioo.mahjong_public_server.util.CardTools;

public class A4BC extends A4 {

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

		A4BC a4bc = (A4BC) arg0;

		return getNum() - a4bc.getNum();
	}

	@Override
	public CardList pattern(CardSort cardSort, List<Integer> arr) throws CardListPatternException {
		if (arr.size() != 6)
			throw new CardListPatternException();

		Set<Integer> set0 = cardSort.getCardSort().get(0);
		Set<Integer> set1 = cardSort.getCardSort().get(1);
		Set<Integer> set3 = cardSort.getCardSort().get(3);
		if (set3.size() != 1)
			throw new CardListPatternException();
		// 如果是带对子

		if (!(set0.size() == 3 ||(set0.size() == 2 && set1.size() == 1)))
			throw new CardListPatternException();
		A4BC a4bc = new A4BC();
		a4bc.setNum(set3.iterator().next());
		for (int i : set0) {
			if (i != a4bc.getNum()) {
				a4bc.getAddNumList().add(i);
			}
		}

		return a4bc;
	}
	
	@Override
	public void recommand(List<List<Integer>> recommandList, CardSort cardSort, CardList lastCardList, List<Integer> arr) {
		Set<Integer> set3 = cardSort.getCardSort().get(3);
		Set<Integer> set0 = cardSort.getCardSort().get(0);
		if (arr.size() < 6 || set3.size() < 1)
			return;
		if (lastCardList == null) {
			
		} else {
			// 被动出牌
			if (lastCardList.getClass() != A4BC.class) {
				
				A4BC a2 = (A4BC) lastCardList;
				int num = a2.getNum();
				for (int i = num + 1; i < 0xE; i++) {
					if (set3.contains(i)) {
						Set<Integer> tSet = cardSort.getCardSort().get(0);
						tSet.remove(i);
						for(int temp : cardSort.getCardSort().get(1)){
							tSet.remove(temp);
						}
						List<Integer> list = new ArrayList<>(6);
						list.add(i);
						list.add(i);
						list.add(i);
						list.add(i);
						
						recommandList.add(list);
					}
				}
			}
		}
	}

}
