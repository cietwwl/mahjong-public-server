package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;


public class Hu extends AbstractCardList {

	private List<Integer> haveCards = new ArrayList<>();

	@Override
	public void check(List<CardList> cardLists, CardSort cardSort, int card, List<CardList> showCardList, boolean isMine) {
		CardSort copySort = fillCards(cardSort, showCardList);

		// 策略为先分别拿出将牌，再查剩下的牌
		Set<Integer> set2 = copySort.getList().get(1);
		for (int value : set2) {
			List<Integer> jiangList = Arrays.asList(value, value);

			copySort.remove(jiangList);
			
			
		}
	}

	private CardSort fillCards(CardSort cardSort, List<CardList> showCardList) {
		CardSort copyCardSort = cardSort.clone();

		for (CardList cardList : showCardList) {
			List<Integer> cards = cardList.getCards();
			copyCardSort.fillCardSort(cards);
		}
		return copyCardSort;
	}

	@Override
	public List<Integer> getCards() {
		// TODO Auto-generated method stub
		return null;
	}

}
