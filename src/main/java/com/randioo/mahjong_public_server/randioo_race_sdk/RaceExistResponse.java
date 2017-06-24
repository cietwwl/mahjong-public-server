package com.randioo.mahjong_public_server.randioo_race_sdk;

import com.google.gson.annotations.SerializedName;

public class RaceExistResponse {

	@SerializedName("errorCode")
	public int errorCode;

	@SerializedName("id")
	public int raceId;

	@SerializedName("game")
	public String raceName;

	@SerializedName("m_time")
	public int deltaTime;

	@SerializedName("gang")
	public int gang;

	@SerializedName("zhuahu")
	public int moHu;
	@SerializedName("kangkai")
	public int gangkai;

	@SerializedName("feicangying")
	public int fly;

	@SerializedName("cangyingfenshu")
	public int flyScore;

	@SerializedName("difen")
	public int difen;

	@SerializedName("create_time")
	public long createTime;

	@SerializedName("rooms")
	public String rooms;

	@SerializedName("nickname")
	public String nickname;

	@SerializedName("reward")
	public int reward;

	@SerializedName("start")
	public int status;

	@SerializedName("end_time")
	public long endTime;

	@SerializedName("line_num")
	public int onlineCount;
}
