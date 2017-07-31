package com.randioo.mahjong_public_server.module.fight.component.cardlist;

import java.util.List;

import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;

public interface CardList {
	public void check(GameConfigData gameConfigData, List<CardList> cardLists, CardSort cardSort, int card,
			List<CardList> showCardList, boolean isMine);

	public List<Integer> getCards();

	public int getTargetSeat();

	public void setTargetSeat(int targetSeat);
}
