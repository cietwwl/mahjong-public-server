package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;

public class ZLPBaiDaHu extends Hu {
	@Override
	public void check(GameConfigData gameConfigData, List<CardList> cardLists, CardSort cardSort, int card,
			List<CardList> showCardList, boolean isMine) {

		Set<Integer> baiDaHu = new HashSet<>();
		baiDaHu.add(801);
		// 1.克隆牌组
		CardSort cardSort1 = cardSort.clone();

		List<Integer> l = cardSort1.toArray();
		Collections.sort(l);
		System.out.println(l);

		// 2.去除所有的白搭
		int baiDaCount = cardSort1.removeAll(801);

		// 3.三个一样的先拿走
		List<Integer> kezi_arr = new ArrayList<>(cardSort1.get(2));
		for (int kezi : kezi_arr)
			cardSort1.remove(kezi, kezi, kezi);

		// 4.从头到尾吃一边
		int step4chiCount = 0;
		{
			List<Integer> cards = cardSort1.toArray();
			// 从小到大排序
			Collections.sort(cards);

			step4chiCount = getChiCountAndRemoveChi(cards, baiDaCount);
			System.out.println("remain=" + cards);
		}

		System.out.println("step4chiCount=" + step4chiCount);

		int step5ChiCount = 0;
		// 5.刻子拿回来再吃一遍,但有四个相同的先拿走三个
		{
			for (int kezi : kezi_arr) {
				cardSort1.addCard(kezi);
				cardSort1.addCard(kezi);
				cardSort1.addCard(kezi);
			}

			List<Integer> gangzi_arr = new ArrayList<>(cardSort1.get(3));
			for (int gangzi : gangzi_arr)
				cardSort1.remove(gangzi, gangzi, gangzi);

			List<Integer> cards = cardSort1.toArray();
			// 从小到大排序
			Collections.sort(cards);

			step5ChiCount = getChiCountAndRemoveChi(cards, baiDaCount);
			System.out.println("remain=" + cards);
		}
		System.out.println("step5ChiCount=" + step5ChiCount);
	}

	private int getChiCountAndRemoveChi(List<Integer> cards, int baiDaCount) {
		List<Integer> result = new ArrayList<>(4);
		int chiCount = 0;
		for (int i = 0; i < cards.size(); i++) {
			int c1 = cards.get(i);
			int c2 = c1 + 1;
			int c3 = c1 + 2;

			// 检查有没有这个吃
			if (cards.contains(c2) && cards.contains(c3)) {
				// 移除吃
				cards.remove(i);
				cards.remove(cards.indexOf(c2));
				cards.remove(cards.indexOf(c3));

				result.add(c1);
				i = -1;
				continue;
			} else if (cards.contains(c2) || cards.contains(c3)) {
				cards.remove(i);
				int c2Index = cards.indexOf(c2);
				int c3Index = cards.indexOf(c3);
				if (c2Index != -1)
					cards.remove(c2Index);
				if (c3Index != -1)
					cards.remove(c3Index);

				if (baiDaCount > 0)
					baiDaCount--;
				result.add(c1);
				i = -1;
				continue;
			}
		}
		System.out.println(result);
		return chiCount;
	}

	@Override
	public void checkTing(CardSort cardSort, List<Integer> waitCards) {

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
		ZLPBaiDaHu hu = new ZLPBaiDaHu();
		CardSort cardSort = new CardSort(4);
		List<Integer> list = Arrays.asList(101, 102, 103, 104, 105, 201, 302, 101, 102, 201, 302, 801, 801, 302);
		// List<Integer> list = Arrays.asList(11, 12, 13, 14, 15, 21, 32, 11,
		// 12, 21, 32, 12, 21, 32);
		cardSort.fillCardSort(list);

		hu.check(null, cardLists, cardSort, 0, null, false);
		System.out.println(cardLists);
	}

}
