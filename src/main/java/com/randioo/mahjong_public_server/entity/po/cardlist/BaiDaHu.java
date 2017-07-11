package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.util.Lists;

public class BaiDaHu extends Hu {

	@Override
	public void check(List<CardList> cardLists, CardSort cardSort, int card, List<CardList> showCardList, boolean isMine) {
		List<Integer> baidaCards = new ArrayList<>();
		baidaCards.add(81);

		CardSort copySort = cardSort.clone();

		// 去除所有白搭，并进行存储
		List<Integer> tempBaiDaCards = new ArrayList<>();

		List<Set<Integer>> list = copySort.getList();

		boolean containsBaiDa;// 是否包含百搭
		do {
			containsBaiDa = false;
			Set<Integer> cards = list.get(0);
			// 遍历所有百搭牌
			for (int baidaCard : baidaCards) {
				if (cards.contains(baidaCard)) {
					tempBaiDaCards.add(baidaCard);
					cards.add(baidaCard);
					containsBaiDa = true;
				}
			}
		} while (containsBaiDa);

		// 取三
		CardSort copySort2 = copySort.clone();
		List<CardList> tempCardList2 = new ArrayList<>();

		this.removePeng(copySort2, tempCardList2);
		this.removeChi(copySort2, tempCardList2);

		CardSort copySort3 = copySort.clone();
		List<CardList> tempCardList3 = new ArrayList<>();

		this.removeChi(copySort3, tempCardList3);
		this.removePeng(copySort3, tempCardList3);

	}

	private boolean ChiPeng(List<CardList> cardLists, CardSort cardSort, int card, List<CardList> showCardList,
			boolean isMine) {
		CardSort copySort3 = cardSort.clone();
		List<CardList> tempCardList3 = new ArrayList<>();
		this.removeChi(copySort3, tempCardList3);
		this.removePeng(copySort3, tempCardList3);

		Set<Integer> set0 = copySort3.get(0);
		if (set0.size() == 0) {
			this.extractedHu(cardLists, cardSort, card, showCardList, isMine);

			return true;
		}

		return false;
	}

	private void extractedHu(List<CardList> cardLists, CardSort cardSort, int card, List<CardList> showCardList,
			boolean isMine) {
		List<Integer> list = cardSort.toArray();

		BaiDaHu hu = new BaiDaHu();
		hu.card = card;
		hu.isMine = isMine;
		Lists.removeElementByList(list, Arrays.asList(card));
		Collections.sort(list);
		hu.handCards.addAll(list);
		hu.showCardList.addAll(showCardList);
		cardLists.add(hu);
	}

	private boolean PengChi(List<CardList> cardLists, CardSort cardSort, int card, List<CardList> showCardList,
			boolean isMine) {
		CardSort copySort3 = cardSort.clone();
		List<CardList> tempCardList3 = new ArrayList<>();
		this.removePeng(copySort3, tempCardList3);
		this.removeChi(copySort3, tempCardList3);

		Set<Integer> set0 = copySort3.get(0);
		if (set0.size() == 0)
			return true;
		return false;
	}

	private void removeChi(CardSort copySort2, List<CardList> tempCardList) {
		Set<Integer> set = copySort2.get(0);
		boolean containsChi = false;
		do {
			containsChi = false;

			List<Integer> values = new ArrayList<>(set);
			if (values.size() == 0) {
				break;
			}
			int card1 = values.get(0);
			int card2 = card1 + 1;
			int card3 = card2 + 1;

			if (set.contains(card2) && set.contains(card3)) {
				Chi chi = new Chi();
				chi.card = card1;
				containsChi = true;

				tempCardList.add(chi);
			}

			copySort2.remove(card1, card2, card3);

		} while (containsChi);
	}

	private void removePeng(CardSort copySort2, List<CardList> tempCardList) {
		Set<Integer> set2 = copySort2.get(2);
		for (int value : set2) {
			Peng peng = new Peng();
			peng.card = value;

			tempCardList.add(peng);
		}

		List<Integer> cards = new ArrayList<>(set2);
		for (int c : cards) {
			copySort2.remove(c, c, c);
		}
	}

	@Override
	public List<Integer> getCards() {
		return null;
	}

	/**
	 * 移除百搭牌
	 * 
	 * @param baidaCards
	 * @param copySort
	 * @param tempBaiDaCards
	 * @author wcy 2017年7月10日
	 */
	private void removeBaida(List<Integer> baidaCards, CardSort copySort, List<Integer> tempBaiDaCards) {
		boolean containsBaiDa;// 是否包含百搭
		do {
			containsBaiDa = false;
			Set<Integer> cards = copySort.get(0);
			// 遍历所有百搭牌
			for (int baidaCard : baidaCards) {
				if (cards.contains(baidaCard)) {
					tempBaiDaCards.add(baidaCard);
					cards.add(baidaCard);
					containsBaiDa = true;
				}
			}
		} while (containsBaiDa);
	}

	@Override
	public void checkTing(CardSort cardSort, List<Integer> waitCards) {
		List<Integer> baidaCards = new ArrayList<>();
		baidaCards.add(81);

		CardSort copySort = cardSort.clone();

		// 去除所有白搭，并进行存储
		List<Integer> tempBaiDaCards = new ArrayList<>();
		this.removeBaida(baidaCards, copySort, tempBaiDaCards);

		// 取三
		CardSort copySort2 = copySort.clone();
		List<CardList> tempCardList2 = new ArrayList<>();

		this.removePeng(copySort2, tempCardList2);
		this.removeChi(copySort2, tempCardList2);

		Set<Integer> two = copySort2.get(1);

		// 如果有将，分别拿除，并加入剩余的红中
		if (copySort2.get(1).size() > 0) {
			for (int card : two) {
				CardSort copySort3 = copySort2.clone();
				// 取出该将牌
				copySort3.remove(card, card);

			}

		} else {
			List<? extends CardList> list = get();
			CardList cardList = list.get(1);
			
			
		}

	}
	
	private List<? extends CardList> get(){
		return new ArrayList<Chi>();
	}

}
