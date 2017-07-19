package com.randioo.mahjong_public_server.module.audience.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.mahjong_public_server.cache.local.RaceCache;
import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.po.Race;
import com.randioo.mahjong_public_server.module.fight.FightConstant;
import com.randioo.mahjong_public_server.module.fight.service.FightService;
import com.randioo.mahjong_public_server.module.match.MatchConstant;
import com.randioo.mahjong_public_server.module.match.service.MatchService;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Observer;
import com.randioo.randioo_server_base.utils.SessionUtils;

@Service("audienceService")
public class AudienceServiceImpl extends ObserveBaseService implements AudienceService {

	@Autowired
	private FightService fightService;

	@Autowired
	private MatchService matchService;

	@Override
	public void initService() {
		fightService.addObserver(this);
		matchService.addObserver(this);
	}

	@Override
	public void update(Observer observer, String msg, Object... args) {
		if (msg.equals(MatchConstant.JOIN_GAME)) {
			SC scJoinGame = (SC) args[0];
			int gameId = (int) args[1];
			
			observer_joinGame(scJoinGame, gameId);
		}

		if (msg.equals(FightConstant.FIGHT_READY)) {
			SC scFightReady = (SC) args[0];
			Game game = (Game) args[1];

			observer_fightReady(scFightReady, game.getGameId());
		}
	}

	private void observer_fightReady(SC scFightReady, int gameId) {
		Race race = RaceCache.getGameRaceMap().get(gameId);
		if (race == null) {
			return;
		}

		List<Integer> roleIdList = race.getRoleIdQueue();
		for (int roleId : roleIdList) {
			SessionUtils.sc(roleId, scFightReady);
		}
	}

	private void observer_joinGame(SC scJoinGame, int gameId) {
		Race race = RaceCache.getGameRaceMap().get(gameId);
		if (race == null) {
			return;
		}

		List<Integer> roleIdList = race.getRoleIdQueue();
		for (int roleId : roleIdList) {
			SessionUtils.sc(roleId, scJoinGame);
		}
	}
}
