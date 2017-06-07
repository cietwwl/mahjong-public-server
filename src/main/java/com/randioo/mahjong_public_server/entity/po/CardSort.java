package com.randioo.mahjong_public_server.entity.po;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CardSort {

	public CardSort() {
		this(1);
	}

	public CardSort(int size) {
		size *= 4;
		for (int i = 0; i < size; i++)
			cardSort.add(new TreeSet<Integer>());
	}

	private List<Set<Integer>> cardSort = new ArrayList<>();

	public List<Set<Integer>> getCardSort() {
		return cardSort;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (Set<Integer> set : cardSort) {
			sb.append(i).append("=");
			for (int value : set)
				sb.append(value).append(" ");
			sb.append("\n");
			i++;
		}
		return sb.toString();
	}

	public CardSort clone() {
		CardSort cardSort = new CardSort(this.cardSort.size() / 4);
		for (int i = 0; i < this.cardSort.size(); i++) {
			Set<Integer> set = this.cardSort.get(i);
			Set<Integer> set2 = cardSort.cardSort.get(i);
			set2.addAll(set);

		}
		return cardSort;
	}

}
