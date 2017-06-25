package com.randioo.mahjong_public_server.entity.po;

import java.util.ArrayList;
import java.util.List;

public class RaceStateInfo {
	public int raceId;
	public List<RaceRole> accounts = new ArrayList<>();
	public List<RaceRole> queueAccount = new ArrayList<>();
	public boolean isFinal;

}
