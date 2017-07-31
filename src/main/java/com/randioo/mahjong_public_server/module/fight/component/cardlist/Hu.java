package com.randioo.mahjong_public_server.module.fight.component.cardlist;

import java.util.ArrayList;
import java.util.List;

import com.randioo.mahjong_public_server.entity.po.CardSort;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;

public abstract class Hu extends AbstractCardList {

	public int card;
	public List<Integer> handCards = new ArrayList<>();
	public List<CardList> showCardList = new ArrayList<>();
	public boolean isMine;
	public boolean gangKai;
	public boolean gangChong;
	public int gangChongTargetSeat;

	public abstract void checkTing(CardSort cardSort, List<Integer> waitCards,GameConfigData gameConfigData);
}
