package com.randioo.mahjong_public_server.module.race.service;

import com.randioo.mahjong_public_server.entity.bo.Role;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface RaceService extends ObserveBaseServiceInterface {

	/**
	 * 比赛初始化
	 * 
	 * @param role
	 * @author wcy 2017年6月22日
	 */
	void raceInit(Role role);

	void newRaceInit(Role role);
	/**
	 * 加入比赛
	 * 
	 * @param role
	 * @param gameId
	 * @author wcy 2017年6月23日
	 */
	void joinRace(Role role, int raceId);

	/**
	 * 踢出比赛
	 * 
	 * @param raceId
	 * @param account
	 * @author wcy 2017年6月28日
	 */
	void kickRace(int raceId, String account);

}
