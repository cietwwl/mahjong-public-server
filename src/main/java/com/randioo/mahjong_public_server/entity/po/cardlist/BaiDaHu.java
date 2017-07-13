package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.util.Lists;

public class BaiDaHu extends Hu {

	public boolean checkBaiDa(CardSort cardSort) {
		// TODO 从配置加载 有四个红中时是否直接胡
		boolean config = true;
		CardSort cardSort1 = cardSort.clone();

		int baiDaCount = cardSort1.count(801); // 红中个数
		if (config) {
			if (baiDaCount == 4)
				return true;
		}
		// 移除红中
		cardSort1.removeAll(801);

		System.out.println("移除红中后");
		System.out.println(cardSort1);

		// 挑3
		CardSort copySort2 = cardSort1.clone();
		List<CardList> tempCardList2 = new ArrayList<>();

		removePeng(copySort2, tempCardList2);
		removeChi(copySort2, tempCardList2);

		CardSort copySort3 = cardSort1.clone();
		List<CardList> tempCardList3 = new ArrayList<>();

		removeChi(copySort3, tempCardList3);
		removePeng(copySort3, tempCardList3);

		CardSort tempCardSort = copySort2.sumCard() <= copySort3.sumCard() ? copySort2 : copySort3;

		System.out.println("挑3后");
		System.out.println("2    " + copySort2);
		System.out.println("3    " + copySort3);
		// 计算需要百搭的个数

		// 二连 个数
		int lianCount = 0;

		for (Set<Integer> set : tempCardSort.getList()) {
			List<Integer> list = new ArrayList<>(set);
			for (int i = 0; i < list.size() - 1; i++) {
				if (list.get(i) + 1 == list.get(i + 1)) {
					lianCount++;

					// 删除一组 二连
					set.remove(list.get(i));
					set.remove(list.get(i) + 1);
				}
			}
		}
		System.out.println("移除二连后");
		System.out.println(tempCardSort);

		// 纯对 个数
		int duiCount = 0;

		List<Integer> list1 = new ArrayList<>();
		list1.addAll(tempCardSort.get(0)); // 第一行set
		List<Integer> list2 = new ArrayList<>();
		list2.addAll(tempCardSort.get(1)); // 第二行set

		for (Integer v : list1) {
			if (list2.contains(v)) {
				duiCount++;
				// 删除对数
				tempCardSort.removeAll(v);
			}
		}
		System.out.println("移除纯对后");
		System.out.println(tempCardSort);

		// 单数个数
		int danCount = tempCardSort.sumCard();

		int needBaiDa = lianCount + duiCount > 0 ? danCount * 2 + duiCount - 1 : danCount * 2 - 1;

		return baiDaCount >= needBaiDa ? true : false;
	}

	@Override
	public void check(List<CardList> cardLists, CardSort cardSort, int card, List<CardList> showCardList,
			boolean isMine) {
		List<Integer> baidaCards = new ArrayList<>();
		baidaCards.add(801);

		this.checkBaiDa(cardSort);

	}

	private void removeChi(CardSort copySort2, List<CardList> tempCardList) {
		Set<Integer> set = copySort2.get(0);

		List<Integer> values = new ArrayList<>(set);
		for (int i = 0; i < values.size(); i++) {

			int card1 = values.get(i);
			int card2 = card1 + 1;
			int card3 = card2 + 1;
			if (set.contains(card2) && set.contains(card3)) {
				Chi chi = new Chi();
				chi.card = card1;

				tempCardList.add(chi);
				copySort2.remove(card1, card2, card3);
				i += 1; // 如果满足，直接跳过2个数
			}
		}
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
	public void checkTing(CardSort cardSort, List<Integer> waitCards) {
		List<Integer> tryCards = new ArrayList<>();
		int start = 100;
		// 把所有牌加入 tryCards
		for (int i = 1; i <= 9; i++) {
			tryCards.add(start + i);
			for (int j = 2; j <= 3; j++)
				tryCards.add(start * j + i);
		}
		tryCards.add(801); // 加入红中

		for (Integer i : tryCards) {
			cardSort.addCard(i);
			if (this.checkBaiDa(cardSort))
				waitCards.add(i);
		}

	}

	public static void main(String[] args) {
		// List<Integer> list1 = Arrays.asList(104, 104, 105, 106);
		// List<Integer> list2 = Arrays.asList(102, 103, 104, 801);
		// List<Integer> list2 = Arrays.asList(202, 206, 104, 801);
		// List<Integer> list3 = Arrays.asList(104, 204, 205, 206);
		// List<Integer> list4 = Arrays.asList(302, 303);
		//
		// CardSort cardSort = new CardSort(4);
		//
		// cardSort.fillCardSort(list1);
		// cardSort.fillCardSort(list2);
		// cardSort.fillCardSort(list3);
		// cardSort.fillCardSort(list4);
		// System.out.println(cardSort);
		Hu hu = new BaiDaHu();
		hu.checkTing(null, null);

	}

	@Override
	public List<Integer> getCards() {
		// TODO Auto-generated method stub
		return null;
	}

}
