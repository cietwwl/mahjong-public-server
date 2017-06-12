package com.randioo.mahjong_public_server.entity.po;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
}
