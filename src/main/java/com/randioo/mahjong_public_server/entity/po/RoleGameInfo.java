package com.randioo.mahjong_public_server.entity.po;

import java.util.ArrayList;
import java.util.List;

public class RoleGameInfo {
	public String gameRoleId;
	public int roleId;
	public boolean ready;
	//public int seatIndex;
	public Boolean agreeLeave;
	public List<Integer> cards = new ArrayList<>();
	public int auto;
}
