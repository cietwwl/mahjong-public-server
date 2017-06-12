package com.randioo.mahjong_public_server.module.race.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.mahjong_public_server.cache.local.RaceCache;
import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.entity.po.MahjongRaceConfigure;
import com.randioo.mahjong_public_server.entity.po.Race;
import com.randioo.mahjong_public_server.module.match.service.MatchService;
import com.randioo.mahjong_public_server.protocol.Entity.GameConfig;
import com.randioo.randioo_server_base.db.IdClassCreator;
import com.randioo.randioo_server_base.protocol.randioo.Message;
import com.randioo.randioo_server_base.service.ObserveBaseService;

@Service("raceService")
public class RaceServiceImpl extends ObserveBaseService implements RaceService {

	@Autowired
	private MatchService matchService;

	@Autowired
	private IdClassCreator idClassCreator;

	@Override
	public Message createRace(MahjongRaceConfigure configure) {

		GameConfig gameConfig = this.parseGameConfig(configure);

		Game game = matchService.createGameByGameConfig(gameConfig);

		// 新建一场比赛
		Race race = createRace(game.getGameId(), configure);

		Message message = new Message();
		message.setType((short) 1);
		message.putInt(1);// success
		message.putInt(race.getGameId());

		return message;
	}

	@Override
	public void raceEnd(int raceId) {
		RaceCache.getRaceMap().remove(raceId);
	}

	private GameConfig parseGameConfig(MahjongRaceConfigure configure) {
		GameConfig gameConfig = GameConfig.newBuilder().setZhuahu(configure.zhuahu).setRaceType(configure.raceType)
				.setMinStartScore(configure.minStartScore).setMaxCount(configure.maxCount)
				.setGangScore(configure.gangScore).setGangkai(configure.gangkai).setCatchScore(configure.catchScore)
				.setEndCatchCount(configure.endCatchCount).build();

		return gameConfig;
	}

	private Race createRace(int gameId, MahjongRaceConfigure configure) {
		// 新建一场比赛
		Race race = new Race();
		race.setRaceName(configure.raceName);
		race.setEndTime(configure.endTime);
		race.setGameId(gameId);
		RaceCache.getRaceMap().put(race.getGameId(), race);

		return race;
	}
}
