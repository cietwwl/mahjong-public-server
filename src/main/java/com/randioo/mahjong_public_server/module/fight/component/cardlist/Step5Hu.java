package com.randioo.mahjong_public_server.module.fight.component.cardlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.mahjong_public_server.util.Lists;

public class Step5Hu extends Hu {
	@Override
	public void check(GameConfigData gameConfigData, List<CardList> cardLists, CardSort cardSort, int card,
			List<CardList> showCardList, boolean isMine) {
		this.isMine = isMine;
		CardSort copySort = cardSort.clone();
		for (int value : copySort.getList().get(1)) {
			System.out.println("===============");
			System.out.println(" value-> " + value);
			System.out.println("===============");
			List<Integer> cards = copySort.toArray();
			Collections.sort(cards);
			System.out.println(cards);
			int step = 1;
			END: {
				while (true) {
					switch (step) {
					case 1: {
						removeDoubleCardStep(value, cards);
						System.out.println(cards);
					}
					case 2: {
						if (checkHuStep(cards)) {
							Step5Hu hu = new Step5Hu();
							List<Integer> list = cardSort.toArray();
							hu.card = card;
							hu.isMine = isMine;
							Lists.removeElementByList(list, Arrays.asList(card));
							Collections.sort(list);
							hu.handCards.addAll(list);
							hu.showCardList.addAll(showCardList);
							cardLists.add(hu);
							break END;
						}
						System.out.println(cards);
					}
					case 3: {
						if (!checkFormer3CardIsSameStep(cards)) {
							step = 5;
							break;
						}
						System.out.println(cards);
					}
					case 4: {
						removeFormer3CardIsSameStep(cards);
						step = 2;
						break;
					}
					case 5: {
						if (removeShunCardsStep(cards)) {
							step = 2;
							break;
						} else {
							break END;
						}
					}
					default:
						System.out.println("hu error");
					}
					System.out.println(cards);
				}
			}
		}
	}

	private void removeDoubleCardStep(int value, List<Integer> cards) {
		step1(Arrays.asList(value, value), cards);
	}

	private void step1(List<Integer> doubleCards, List<Integer> cards) {
		// 移除将牌
		Lists.removeElementByList(cards, doubleCards);
		System.out.println("移除将牌");
	}

	private boolean checkHuStep(List<Integer> cards) {
		return step2(cards);
	}

	private boolean step2(List<Integer> cards) {
		boolean result = cards.size() == 0;
		System.out.println("检查是否剩余牌数为0,如果是则胡牌");
		return result;
	}

	private boolean checkFormer3CardIsSameStep(List<Integer> cards) {
		return step3(cards);
	}

	private boolean step3(List<Integer> cards) {
		if (cards.size() < 3)
			return false;
		
		int card1 = cards.get(0);
		int card2 = cards.get(1);
		int card3 = cards.get(2);

		boolean result = card1 == card2 && card2 == card3;
		System.out.println("检查前三张牌是否相同");
		return result;
	}

	private void removeFormer3CardIsSameStep(List<Integer> cards) {
		step4(cards);
	}

	private void step4(List<Integer> cards) {
		for (int i = 0; i < 3; i++)
			cards.remove(0);
		System.out.println("移除前三张牌");
	}

	private boolean removeShunCardsStep(List<Integer> cards) {
		return step5(cards);
	}

	private boolean step5(List<Integer> cards) {
		int card1 = cards.get(0);
		int card2 = card1 + 1;
		int card3 = card1 + 2;
		if (cards.contains(card2) && cards.contains(card3)) {
			Lists.removeElementByList(cards, Arrays.asList(card1));
			Lists.removeElementByList(cards, Arrays.asList(card2));
			Lists.removeElementByList(cards, Arrays.asList(card3));
			System.out.println("移除顺牌");
			return true;
		}
		System.out.println("移除顺牌");
		return false;
	}

	/**
	 * 获得百搭牌的数量
	 * 
	 * @param cardSort
	 * @return
	 * @author wcy 2017年6月21日
	 */
	private int getKingCardCount(CardSort cardSort) {
		List<Set<Integer>> list = cardSort.getList();
		int j = list.size();
		for (int i = list.size() - 1; i >= 0; i--) {
			Set<Integer> set = list.get(i);
			if (set.contains(801)) {
				break;
			} else {
				j--;
			}
		}
		return j;
	}

	

	@Override
	public String toString() {
		return "cardList:hu=>gangkai=" + gangKai + ",isMine=" + isMine + ",card=" + card + "," + super.toString();
	}

	@Override
	public List<Integer> getCards() {
		return null;
	}

	public static void main(String[] args) {
		List<CardList> cardLists = new ArrayList<>();
		Step5Hu hu = new Step5Hu();
		CardSort cardSort = new CardSort(4);
		List<Integer> list = Arrays.asList(101, 102, 102, 103, 104, 104, 105, 106, 107, 107, 108, 109, 109, 801);
		// List<Integer> list = Arrays.asList(11, 12, 13, 14, 15, 21, 32, 11,
		// 12, 21, 32, 12, 21, 32);
		cardSort.fillCardSort(list);

		int i = hu.getKingCardCount(cardSort);
		System.out.println(i);
		Set<Integer> array = new HashSet<>();
		array.add(11);
		array.add(12);
		array.add(13);

		Set<Integer> array1 = new HashSet<>();
		array1.add(11);
		array1.add(12);
		array1.add(13);

		 hu.check(null,cardLists, cardSort, 0, null, false);
		// System.out.println(cardLists);
	}

	private void cal(Set<Integer> targetSet, int count) {

		// 81,81,81,11,12
		// 81,81,11,11,12
		// 81,81,12,11,12
		// 81,11,11,11,12
		// 81,12,11,11,12
		// 11,11,11,11,12
		// 12,11,11,11,12
		// 11,12,11,11,12
		// 11,11,12,12,12
		List<Integer> change = new ArrayList<>();

	}

	@Override
	public void checkTing(CardSort cardSort, List<Integer> waitCards, GameConfigData gameConfigData) {
		// TODO Auto-generated method stub
		
	}

}
