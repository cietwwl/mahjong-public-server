package com.randioo.mahjong_public_server.entity.po;

import com.randioo.randioo_server_base.scheduler.DefaultTimeEvent;
import com.randioo.randioo_server_base.scheduler.TimeEvent;

public abstract class AISendCardTimeEvent extends DefaultTimeEvent {

	private int gameId;

	@Override
	public abstract void update(TimeEvent timeEvent);

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

}
