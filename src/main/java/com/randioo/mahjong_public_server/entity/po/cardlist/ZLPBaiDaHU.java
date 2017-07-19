package com.randioo.mahjong_public_server.entity.po.cardlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;

public class ZLPBaiDaHU extends Hu {
	@Override
	public void check(GameConfigData gameConfigData, List<CardList> cardLists, CardSort cardSort, int card,
			List<CardList> showCardList, boolean isMine) {

		Set<Integer> baiDaHu = new HashSet<>();
		baiDaHu.add(801);
		// 1.先去除所有的碰
		CardSort cardSort1 = cardSort.clone();
		List<Integer> pengStoreNums = new ArrayList<>(cardSort1.get(2).size());
		List<Integer> pengNums = new ArrayList<>(cardSort1.get(2));
		for (int num : pengNums) {
			if (baiDaHu.contains(num))
				continue;

			pengStoreNums.add(num);
			cardSort.remove(num, num, num);
		}

		List<Integer> cards = cardSort1.toArray();
		Collections.sort(cards);
		
		for(int i = 0;i<cards.size();i++){
			
		}
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
		// List<CardList> cardLists = new ArrayList<>();
		// ZLPBaiDaHU hu = new ZLPBaiDaHU();
		// CardSort cardSort = new CardSort(4);
		// List<Integer> list = Arrays.asList(11, 12, 13, 14, 15, 21, 32, 11,
		// 12, 21, 32, 81, 81, 32);
		// // List<Integer> list = Arrays.asList(11, 12, 13, 14, 15, 21, 32, 11,
		// // 12, 21, 32, 12, 21, 32);
		// cardSort.fillCardSort(list);
		//
		// int i = hu.getKingCardCount(cardSort);
		// System.out.println(i);
		// Set<Integer> array = new HashSet<>();
		// array.add(11);
		// array.add(12);
		// array.add(13);
		//
		// Set<Integer> array1 = new HashSet<>();
		// array1.add(11);
		// array1.add(12);
		// array1.add(13);

		// hu.check(cardLists, cardSort, 0, null, false);
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

}
