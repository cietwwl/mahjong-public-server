package com.randioo.mahjong_public_server.entity.po;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class RaceStateInfo {
	@SerializedName("raceId")
	public int raceId;
	@SerializedName("playList")
	public List<RaceRole> accounts = new ArrayList<>();
	@SerializedName("waitList")
	public List<RaceRole> queueAccount = new ArrayList<>();
	@SerializedName("isFinal")
	public int isFinal;

}
