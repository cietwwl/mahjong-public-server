package com.randioo.mahjong_public_server.module.fight.component.cardlist;

public abstract class AbstractCardList implements CardList {

	protected int targetSeat;

	public int getTargetSeat() {
		return targetSeat;
	}

	public void setTargetSeat(int targetSeat) {
		this.targetSeat = targetSeat;
	}

	@Override
	public String toString() {
		return "targetSeat=" + targetSeat;
	}

}
