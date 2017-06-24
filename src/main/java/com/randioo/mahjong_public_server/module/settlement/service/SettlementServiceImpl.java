package com.randioo.mahjong_public_server.module.settlement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.mahjong_public_server.module.fight.service.FightService;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Observer;

@Service("settlementService")
public class SettlementServiceImpl extends ObserveBaseService implements SettlementService {

	@Autowired
	private FightService fightService;

	@Override
	public void initService() {
		fightService.addObserver(this);
	}

	@Override
	public void update(Observer observer, String msg, Object... args) {

	}

}
