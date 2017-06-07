package com.randioo.mahjong_public_server.module.score.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.mahjong_public_server.cache.local.GameCache;
import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.po.cardlist.CardList;
import com.randioo.mahjong_public_server.module.fight.FightConstant;
import com.randioo.mahjong_public_server.module.fight.service.FightService;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Observer;

@Service("scoreService")
public class ScoreServiceImpl extends ObserveBaseService implements ScoreService {

	@Autowired
	private FightService fightService;

	@Override
	public void initService() {
		fightService.addObserver(this);
	}

	@Override
	public void update(Observer observer, String msg, Object... args) {
		if (msg.equals(FightConstant.SEND_CARD)) {
			int gameId = (int) args[0];
			String gameRoleId = (String) args[1];
			CardList sendCardList = (CardList) args[2];

			this.updateScore(gameId, gameRoleId, sendCardList);
		}
	}

	private void updateScore(int gameId, String gameRoleId, CardList sendCardList) {
		// 每次出牌后,计算分数
		Game game = GameCache.getGameMap().get(gameId);
		
	}
}
