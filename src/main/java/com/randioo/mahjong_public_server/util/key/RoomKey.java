package com.randioo.mahjong_public_server.util.key;

public class RoomKey extends Key {
	private int gameId;

	public RoomKey() {
		this.gameId = -1;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getGameId() {
		return gameId;
	}

	@Override
	protected void reset() {
		gameId = -1;
	}
}
