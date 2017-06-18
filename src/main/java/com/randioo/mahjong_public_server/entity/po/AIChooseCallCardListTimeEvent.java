package com.randioo.mahjong_public_server.entity.po;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.scheduler.DefaultTimeEvent;
import com.randioo.randioo_server_base.scheduler.TimeEvent;

public abstract class AIChooseCallCardListTimeEvent extends DefaultTimeEvent {

	protected int gameId;

	protected GeneratedMessage message;

	protected int AISeat;

	@Override
	public abstract void update(TimeEvent timeEvent);

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public void setMessage(GeneratedMessage message) {
		this.message = message;
	}

	public void setAISeat(int aISeat) {
		AISeat = aISeat;
	}

}
