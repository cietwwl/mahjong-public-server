package com.randioo.mahjong_public_server.entity.bo;

import java.util.List;

import com.randioo.mahjong_public_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.db.DataEntity;

public class VideoData extends DataEntity{
	private int id;
	private int roleId;
	private int gameId;
	private List<List<SC>> scList ; // index = 0 ,记录场上人员 进场信息
	private byte[] data ; // 将所有sc 转化成String 存入数据库

	public void setData(byte[] data) {
		this.data = data;
	}
	public byte[] getData() {
		return data;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRoleId() {
		return roleId;
	}
	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}
	public int getGameId() {
		return gameId;
	}
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	public List<List<SC>> getScList() {
		return scList;
	}
	public void setScList(List<List<SC>> scList) {
		this.scList = scList;
	}
}
