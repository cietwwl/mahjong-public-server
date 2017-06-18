package com.randioo.mahjong_public_server.entity.po;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.randioo.mahjong_public_server.entity.po.cardlist.CardList;
import com.randioo.mahjong_public_server.entity.po.cardlist.Peng;

public class CardSort {
	public CardSort(int capacity) {
		for (int i = 0; i < capacity; i++)
			values.add(new TreeSet<Integer>());
	}

	private List<Set<Integer>> values = new ArrayList<>();

	public List<Set<Integer>> getList() {
		return values;
	}

	public void fillCardSort(List<Integer> cards) {
		for (int card : cards) {
			for (Set<Integer> set : values) {
				if (set.contains(card)) {
					continue;
				} else {
					set.add(card);
					break;
				}
			}
		}
	}

	public CardSort clone() {
		CardSort cardSort = new CardSort(this.values.size());
		for (int i = 0; i < 4; i++)
			cardSort.values.get(i).addAll(this.values.get(i));

		return cardSort;
	}

	public static void main(String[] args) {
		CardSort cardSort = new CardSort(4);
		List<CardList> list = new ArrayList<>();
		List<Integer> cards = new ArrayList<>();
		cards.add(12);
		cards.add(14);
		cards.add(15);
		cards.add(17);
		cards.add(17);
		cards.add(22);
		cards.add(23);
		cards.add(26);
		cards.add(33);
		cards.add(34);
		cards.add(36);
		cards.add(38);
		cards.add(81);
		cardSort.fillCardSort(cards);
		System.out.println(cardSort);
		Peng peng = new Peng();
		peng.check(list, cardSort, 17);
		System.out.println(list);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Set<Integer> set : this.values) {
			sb.append(set);
		}
		return sb.toString();
	}
}
