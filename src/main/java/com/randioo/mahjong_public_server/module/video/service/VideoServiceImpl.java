package com.randioo.mahjong_public_server.module.video.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.mahjong_public_server.entity.bo.Game;
import com.randioo.mahjong_public_server.module.fight.FightConstant;
import com.randioo.mahjong_public_server.module.fight.service.FightService;
import com.randioo.mahjong_public_server.module.match.service.MatchService;
import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Observer;

@Service("videoService")
public class VideoServiceImpl extends ObserveBaseService implements VideoService {

	@Autowired
	private MatchService matchService;

	@Autowired
	private FightService fightService;

	@Override
	public void initService() {
		matchService.addObserver(this);
		fightService.addObserver(this);
	}

	@Override
	public void update(Observer observer, String msg, Object... args) {
		if (FightConstant.FIGHT_READY.equals(msg)) {
			SC sc = (SC)args[0];
			int gameId = (int)args[1];
			Game game = fightService.getGameById(gameId);
			
			
		}
	}
	
	

}
