package com.randioo.mahjong_public_server.module.fight.component.cardlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.mahjong_public_server.util.Lists;

public class BaiDaHu extends Hu {

	public boolean checkBaiDa(CardSort cardSort) {
		List<Integer> list = cardSort.toArray();
		Collections.sort(list);
		for (int i = 0; i < list.size() - 1; i++) {
			Integer card1 = list.get(i);
			if (Collections.frequency(list, card1) >= 2) {
				List<Integer> list2 = new ArrayList<>(list);
				list2.remove(card1);
				list2.remove(card1);
				System.out.println("=================>");
				System.out.println("删除了对子: " + card1);
				System.out.println(list2);
				System.out.println("=================>");
				if (check3n(list2)) {
					System.out.println("成功");
					return true;
				}
			} else {
				if (Collections.frequency(list, 801) > 0) { // 有百搭
					List<Integer> list2 = new ArrayList<>(list);
					list2.remove(card1);
					list2.remove(new Integer(801));
					System.out.println("删除了普通和百搭: " + card1);
					System.out.println(list2);
					if (check3n(list2)) {
						System.out.println("成功");
						return true;
					}
				}
			}
		}
		return false;

	}

	public boolean check3n(List<Integer> cards) {

		if (cards.size() == 0)
			return true;

		List<Integer> temp = new ArrayList<>(cards);
		if (removeChi(cards)) {
			if (check3n(cards)) {
				return true;
			}
		}
		cards = temp;
		if (removePeng(cards)) {
			if (check3n(cards)) {
				return true;
			}
		}
		return false;

	}

	public boolean removeChi(List<Integer> cards) {
		int baidaCount = Collections.frequency(cards, 801);
		Integer baida = new Integer(801);
		for (int i = 0; i < cards.size(); i++) {
			Integer card1 = cards.get(i);
			Integer card2 = card1 + 1;
			Integer card3 = card2 + 1;
			if (cards.contains(card2) && cards.contains(card3)) {
				cards.remove(card1);
				cards.remove(card2);
				cards.remove(card3);
				System.out.printf("remove: %d %d %d", card1, card2, card3);
				System.out.println();
				System.out.println(cards);
				return true;
			}
		}
		if (baidaCount >= 1) {
			for (int i = 0; i < cards.size(); i++) {
				Integer card1 = cards.get(i);
				Integer card2 = card1 + 1;
				Integer card3 = card1 + 2;
				/**
				 * 1 2 801 去除9 10 801 和 8 9 801的情况
				 */
				if (cards.contains(card2)) {
					cards.remove(card1);
					cards.remove(card2);
					cards.remove(baida);
					System.out.printf("remove: %d %d %d", card1, card2, baida);
					System.out.println();
					System.out.println(cards);
					return true;
				} else if (cards.contains(card3)) { // 1 801 3
					cards.remove(card1);
					cards.remove(card3);
					cards.remove(baida);
					System.out.printf("remove: %d %d %d", card1, card3, baida);
					System.out.println();
					System.out.println(cards);
					return true;
				}
			}
		}
		if (baidaCount >= 2) {
			cards.remove(0);
			cards.remove(baida);
			cards.remove(baida);
			System.out.println("移除1个顺子和2个801");
			System.out.println(cards);
			return true;
		}
		if (baidaCount >= 3) {
			cards.remove(baida);
			cards.remove(baida);
			cards.remove(baida);
			System.out.println("移除0个顺子和3个801");
			System.out.println(cards);
			return true;
		}
		return false;
	}

	public boolean removePeng(List<Integer> cards) {
		CardSort cardSort = new CardSort(4);
		cardSort.fillCardSort(cards);

		int baidaCount = cardSort.count(801);
		if (!cardSort.get(2).isEmpty()) { // 有3个一样的
			Integer card = cardSort.get(2).iterator().next();
			cards.remove(card);
			cards.remove(card);
			cards.remove(card);
			System.out.printf("remove: %d %d %d", card, card, card);
			System.out.println();
			System.out.println(cards);

			return true;
		} else if (!cardSort.get(1).isEmpty()) { // 有两个一样的
			if (baidaCount <= 0) {
				return false;
			} else {
				Integer card = cardSort.get(1).iterator().next();
				cards.remove(card);
				cards.remove(card);
				cards.remove(new Integer(801));
				System.out.printf("remove: %d %d %d", card, card, 801);
				System.out.println();
				System.out.println(cards);

				return true;
			}
		}
		return false;

	}

	@Override
	public void check(GameConfigData gameConfigData, List<CardList> cardLists, CardSort cardSort, int card,
			List<CardList> showCardList, boolean isMine) {

		List<Integer> baidaCards = new ArrayList<>();
		baidaCards.add(801);

		if (this.checkBaiDa(cardSort)) {
			List<Integer> list = cardSort.toArray();
			Lists.removeElementByList(list, Arrays.asList(card));
			BaiDaHu hu = new BaiDaHu();
			hu.isMine = isMine;
			hu.card = card;
			hu.showCardList.addAll(showCardList);
			hu.handCards.addAll(list);
			cardLists.add(hu);
		}

	}

	@Override
	public void checkTing(CardSort cardSort, List<Integer> waitCards, GameConfigData gameConfigData) {
		List<Integer> tryCards = new ArrayList<>();
		int start = 100;
		// 把所有牌型加入 tryCards
		for (int i = 1; i <= 9; i++) {
			int card = start + i;
			if (cardSort.count(card) < 4) { // 没超过四张才能加入
				tryCards.add(card);
			}
			for (int j = 2; j <= 3; j++) {
				card = start * j + i;
				if (cardSort.count(card) < 4) {
					tryCards.add(card);
				}
			}
		}
		if (cardSort.count(801) < 4) {
			tryCards.add(801);
		}

		for (Integer i : tryCards) {
			cardSort.addCard(i);
			System.out.println("加入了" + i);
			if (this.checkBaiDa(cardSort)) {
				waitCards.add(i);
			}
			cardSort.remove(i);
		}
		System.out.println("res:  " + waitCards);
	}

	public void removeCards(List<Integer> cards, Integer... delCards) {
		for (Integer card : delCards) {
			cards.remove(card);
		}
	}

	public static void main(String[] args) {
		// 1 2 2 3 4 4 5 6 7 7 8 9 9
		// 1,2,4,4,6,7,7,7,8,9,9
		// List<Integer> list1 = Arrays.asList(101, 101, 101, 103);
		// List<Integer> list2 = Arrays.asList(103, 105, 106, 108);
		// List<Integer> list3 = Arrays.asList(108, 108, 109);
		// List<Integer> list4 = Arrays.asList(109, 801, 801);

		// List<Integer> list1 = Arrays.asList(101, 102, 104, 104);
		// List<Integer> list2 = Arrays.asList(106, 107, 107, 107);
		// List<Integer> list3 = Arrays.asList(108, 109, 109);
		// List<Integer> list4 = Arrays.asList(801, 801);
		//

		List<Integer> list1 = Arrays.asList(101, 103, 104, 106);
		List<Integer> list2 = Arrays.asList(107, 107, 108, 108);
		List<Integer> list3 = Arrays.asList(109, 201, 203, 801);
		List<Integer> list4 = Arrays.asList(801, 801);

		CardSort cardSort = new CardSort(4);

		cardSort.fillCardSort(list1);
		cardSort.fillCardSort(list2);
		cardSort.fillCardSort(list3);
		cardSort.fillCardSort(list4);
		BaiDaHu hu = new BaiDaHu();
		List<Integer> list = cardSort.toArray();
		Collections.sort(list);
		System.out.println(list);

		long start = System.currentTimeMillis();
		List<Integer> waitList = new ArrayList<>();
		hu.checkBaiDa(cardSort);
		// hu.checkTing(cardSort, waitList, null);
		// System.out.println(hu.checkBaiDa(cardSort));
		long end = System.currentTimeMillis();
		System.out.println(end - start);

	}

	@Override
	public List<Integer> getCards() {
		// TODO Auto-generated method stub
		return null;
	}

}
