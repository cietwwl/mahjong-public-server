package com.randioo.mahjong_public_server.entity.po;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.randioo.mahjong_public_server.entity.po.cardlist.CardList;

public class CallCardListsAction {
	public Map<Class<? extends CardList>, List<CallCardList>> callCardLists = new HashMap<>();
	public boolean guo;
	public boolean decide;
	public int seatedIndex;
}
