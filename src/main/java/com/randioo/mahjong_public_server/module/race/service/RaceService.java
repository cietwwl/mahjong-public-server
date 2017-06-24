package com.randioo.mahjong_public_server.module.race.service;

import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.mahjong_public_server.entity.po.MahjongRaceConfigure;
import com.randioo.randioo_server_base.protocol.randioo.Message;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface RaceService extends ObserveBaseServiceInterface {

	/**
	 * 比赛初始化
	 * 
	 * @param role
	 * @author wcy 2017年6月22日
	 */
	void raceInit(Role role);

	/**
	 * 创建比赛
	 * 
	 * @param configure
	 * @return
	 * @author wcy 2017年6月9日
	 */
	public Message createRace(MahjongRaceConfigure configure);

	void raceEnd(int raceId);

	/**
	 * 加入比赛
	 * @param role
	 * @param gameId
	 * @author wcy 2017年6月23日
	 */
	void joinRace(Role role, int gameId);
}
