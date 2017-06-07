package com.randioo.mahjong_public_server.module.gm.service;

import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface GmService extends ObserveBaseServiceInterface{

	void loopSaveData(boolean mustSave);
//	public GeneratedMessage rejectLogin(String code);
//	public void terminatedServer(String code,IoSession session);
//	public GeneratedMessage openLogin(String code);
}
