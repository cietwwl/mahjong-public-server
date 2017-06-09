package com.randioo.mahjong_public_server.module.race.service;

import com.randioo.mahjong_public_server.entity.po.MahjongRaceConfigure;
import com.randioo.randioo_server_base.protocol.randioo.Message;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface RaceService extends ObserveBaseServiceInterface {
	/**
	 * 创建比赛
	 * @param configure
	 * @return
	 * @author wcy 2017年6月9日
	 */
	public Message createRace(MahjongRaceConfigure configure);

	void raceEnd(int raceId);
}
